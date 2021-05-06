package edu.hubu.mall.webController;

import edu.hubu.mall.entity.CategoryEntity;
import edu.hubu.mall.service.CategoryService;
import edu.hubu.mall.vo.Catalog2Vo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

/**
 * @Author: huxiaoge
 * @Date: 2021/4/28
 * @Description: 商品服务前端界面-首页接入层
 **/
@Controller
public class IndexController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping({"/","/index.html"})
    public String index(Model model){
        List<CategoryEntity> categoryEntities = categoryService.queryTopCategories();
        model.addAttribute("categories",categoryEntities);
        return "index";
    }

    /**
     * 根据前端要求格式返回三级菜单数据
     */
    @GetMapping("/index/catalog.json")
    @ResponseBody
    public Map<String, List<Catalog2Vo>> getCatalogJson(){
        return categoryService.queryCatalogJson();
    }

}
