package edu.hubu.mall.cart.service.impl;

import com.alibaba.fastjson.JSON;
import edu.hubu.mall.cart.feign.ProductFeignService;
import edu.hubu.mall.cart.interceptor.CartLoginInterceptor;
import edu.hubu.mall.cart.service.CartService;
import edu.hubu.mall.cart.vo.Cart;
import edu.hubu.mall.cart.vo.CartItem;
import edu.hubu.mall.common.auth.HostHolder;
import edu.hubu.mall.common.constant.CartConstant;
import edu.hubu.mall.common.exception.MallException;
import edu.hubu.mall.common.order.OrderItemVo;
import edu.hubu.mall.common.product.SkuInfoVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

/**
 * @Author: huxiaoge
 * @Date: 2021/5/31
 * @Description:
 **/
@Service
@Slf4j
public class CartServiceImpl implements CartService {

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    ProductFeignService productFeignService;

    @Autowired
    ThreadPoolExecutor executor;

    /**
     * 添加商品到购物车
     * 使用异步编排方式
     * @param skuId 商品id
     * @param num 商品件数
     * @return
     */
    @Override
    public CartItem addToCart(Long skuId, Integer num) throws ExecutionException, InterruptedException {
        //获取要操作的购物车
        BoundHashOperations<String, Object, Object> opsCart = getOpsCart();

        String redisSku = (String)opsCart.get(skuId.toString());
        CartItem cartItem;
        if(StringUtils.isEmpty(redisSku)){
            //添加商品
            cartItem = new CartItem();
            ;
            CompletableFuture<Void> skuInfoFuture = CompletableFuture.runAsync(() -> {
                SkuInfoVo skuInfo = productFeignService.querySkuInfoById(skuId);
                cartItem.setSkuId(skuId);
                cartItem.setCheck(true);
                cartItem.setCount(num);
                cartItem.setImage(skuInfo.getSkuDefaultImg());
                cartItem.setPrice(skuInfo.getPrice());
                cartItem.setTitle(skuInfo.getSkuTitle());
            }, executor).exceptionally(th -> {
                log.info("添加购物车异常：" + th.getMessage());
                return null;
            });

            CompletableFuture<Void> saleAttrFuture = CompletableFuture.runAsync(() -> {
                //查询商品的销售属性信息
                List<String> attrStr = productFeignService.querySkuSaleAttrValues(skuId);
                cartItem.setSkuAttr(attrStr);
            },executor);

            CompletableFuture.allOf(skuInfoFuture,saleAttrFuture).get();
            String s = JSON.toJSONString(cartItem);
            opsCart.put(skuId.toString(),s);
        }else{
            cartItem = JSON.parseObject(redisSku, CartItem.class);
            cartItem.setCount(cartItem.getCount() +  num);
            String cartItemStr = JSON.toJSONString(cartItem);
            opsCart.put(skuId.toString(),cartItemStr);
        }
        return cartItem;
    }

    /**
     * 查询购物车数据
     * @return
     */
    @Override
    public Cart getMyCart() throws ExecutionException, InterruptedException {
        Cart cart = new Cart();
        //判断用户的登录状态
        HostHolder hostHolder = CartLoginInterceptor.threadLocal.get();
        if(hostHolder.getUserId() != null){
            //登录,需要合并临时购物车的数据
            //先判断临时购物车是否有数据
            String cartKey =  CartConstant.CART_PREFIX + hostHolder.getUserKey();
            List<CartItem> tempCartItems = getCartItems(cartKey);
            //有数据合并临时购物车
            if(tempCartItems != null){
                for (CartItem item:tempCartItems) {
                    addToCart(item.getSkuId(),item.getCount());
                }
                //合并完成，清空购物车
                clearCartInfo(cartKey);
            }
            //合并之后获取用户登录购物车的数据
            String loginCartKey = CartConstant.CART_PREFIX + hostHolder.getUserId();
            List<CartItem> cartItems = getCartItems(loginCartKey);
            cart.setItems(cartItems);
        }else{
            //未登录，获取当前用户的临时购物车
           String cartKey =  CartConstant.CART_PREFIX + hostHolder.getUserKey();
           List<CartItem> cartItems = getCartItems(cartKey);
           cart.setItems(cartItems);
        }
        return cart;
    }

    /**
     * 根据skuId获取指定的购物项的数据
     * @param skuId
     * @return
     */
    @Override
    public CartItem getCartItem(Long skuId) {
        BoundHashOperations<String, Object, Object> opsCart = getOpsCart();
        String str = (String)opsCart.get(skuId.toString());
        return JSON.parseObject(str,CartItem.class);
    }

    /**
     * 清空购物车
     * @param cartKey 购物车的键
     */
    @Override
    public void clearCartInfo(String cartKey) {
        redisTemplate.delete(cartKey);
    }

    /**
     * 切换购物车中购物项的选中状态
     * @param skuId
     * @param check
     */
    @Override
    public void checkCartItem(Long skuId, Integer check) {

        CartItem cartItem = getCartItem(skuId);
        cartItem.setCheck(check == 1);
        BoundHashOperations<String, Object, Object> opsCart = getOpsCart();
        opsCart.put(skuId.toString(),JSON.toJSONString(cartItem));

    }

    @Override
    public void changeItemCount(Long skuId, Integer num) {
        CartItem cartItem = getCartItem(skuId);
        cartItem.setCount(num);
        BoundHashOperations<String, Object, Object> opsCart = getOpsCart();
        opsCart.put(skuId.toString(),JSON.toJSONString(cartItem));
    }

    /**
     * 删除购物项
     * @param skuId
     */
    @Override
    public void deleteItem(Long skuId) {
        BoundHashOperations<String, Object, Object> opsCart = getOpsCart();
        opsCart.delete(skuId.toString());
    }

    /**
     * 查询用户订单结算页的购物项信息
     * 1.调用这个方法时用户肯定是登录状态
     * 2.只需要用户选中的购物项
     * 3.选中的购物项的价格应该是商品的实时价格
     * @return
     */
    @Override
    public List<OrderItemVo> queryMemberCartItems() {
        //当前用户信息
        HostHolder hostHolder = CartLoginInterceptor.threadLocal.get();
        if(hostHolder.getUserId() == null){
            return null;
        }
        String cartKey = CartConstant.CART_PREFIX + hostHolder.getUserId();
        List<CartItem> cartItems = getCartItems(cartKey);
        if(CollectionUtils.isEmpty(cartItems)){
            return null;
        }
        List<CartItem> filterItems = cartItems.stream().filter(CartItem::isCheck).collect(Collectors.toList());
        return filterItems.stream().map(item -> {
            //实时查询商品的价格
            SkuInfoVo skuInfo = productFeignService.querySkuInfoById(item.getSkuId());
            OrderItemVo orderItem = new OrderItemVo();
            orderItem.setPrice(skuInfo.getPrice());
            orderItem.setSkuId(item.getSkuId());
            orderItem.setTitle(item.getTitle());
            orderItem.setSkuAttr(item.getSkuAttr());
            orderItem.setCount(item.getCount());
            orderItem.setImage(item.getImage());
            return orderItem;
        }).collect(Collectors.toList());

    }


    /**
     * 获取要操作的用户的购物车,未在当前线程中获取到用户信息那么使用用户的临时购物车
     * @return
     */
    private BoundHashOperations<String, Object, Object> getOpsCart(){
        HostHolder hostHolder = CartLoginInterceptor.threadLocal.get();
        String cartKey = CartConstant.CART_PREFIX;
        if(hostHolder.getUserId() != null){
            cartKey = cartKey + hostHolder.getUserId();
        }else{
            cartKey += hostHolder.getUserKey();
        }
        return redisTemplate.boundHashOps(cartKey);
    }

    /**
     * 根据指定的key获取购物车中的数据
     * @param cartKey
     * @return
     */
    private List<CartItem> getCartItems(String cartKey){
        BoundHashOperations<String, Object, Object> hashOps = redisTemplate.boundHashOps(cartKey);
        List<Object> values = hashOps.values();
        if(!CollectionUtils.isEmpty(values)){
            List<CartItem> collect = values.stream().map(item -> {
                String str = (String) item;
                return JSON.parseObject(str, CartItem.class);
            }).collect(Collectors.toList());
            return collect;
        }
        return null;
    }
}
