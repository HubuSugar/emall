package edu.hubu.mall.search.thread;

import java.util.concurrent.*;

/**
 * @Author: huxiaoge
 * @Date: 2021/5/17
 * @Description: 多线程测试类
 **/
public class ThreadTest {
    public static ExecutorService executors01 = Executors.newFixedThreadPool(5);
    public static ThreadPoolExecutor executors = new ThreadPoolExecutor(
            10,
            200,
            10,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(10000),
            Executors.defaultThreadFactory(),
            new ThreadPoolExecutor.AbortPolicy());


    public  void thread(String[] args) throws ExecutionException, InterruptedException {
        System.out.println("-------------------main方法开始---------------");

        /**
         * 开启多线程的方法01
         */
        // Thread01 thread01 = new Thread01();
        // thread01.start();
        /**
         * 开启多线程的方法02
         */
        //Thread thread = new Thread(new Thread02());
        //  thread.start();
        /**
         * 开启多线程的方法03,
         * 可以拿到线程执行的返回值
         */
//         FutureTask<Integer> future = new FutureTask<>(new Thread03());
//         new Thread(future).start();
//        try {
//             System.out.println("线程执行的结果为：" + future.get());
//         } catch (InterruptedException | ExecutionException e) {
//             e.printStackTrace();
//         }
        /**
         * 开启多线程的方法04,
         * 线程池
         * corePoolSize:核心线程数
         * maxPoolSize:最大线程数
         * keepAliveTime:线程空闲时间
         * timeUnit:时间单位
         * workQueue:阻塞队列
         * threadFactory:线程池
         * rejectPolicy:拒绝策略
         */
//        executorService.execute(new Thread01());
//        Future<Integer> submit = executorService.submit(new Thread03());
//        System.out.println(submit.get());
//        Future<Integer> submit1 = poolExecutor.submit(new Thread03());
//        System.out.println(submit1.get());

        /**
         * 常用的几种线程池
         */
//        Executors.newFixedThreadPool(5);
//        Executors.newScheduledThreadPool(5);
//        Executors.newCachedThreadPool();
//        Executors.newSingleThreadExecutor();
//        Executors.newWorkStealingPool();

        System.out.println("-------------------main方法结束---------------");
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        System.out.println("-------------------main方法开始---------------");

        //future可以获取到异步任务的结果
        /**
         * 创建CompletableFuture对象
         * runAsync 创建没有返回的异步任务
         */
//        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
//            System.out.println("当前线程开始。。。");
//            int i = 10 / 4;
//            System.out.println("当前线程结束。。。" + 1);
//        }, executors);
        /**
         * supplyAsync 创建又返回值的异步任务
         */
//        CompletableFuture<Integer> future01 = CompletableFuture.supplyAsync(() -> {
//            System.out.println("当前线程开始。。。");
//            int i = 10 / 4;
//            System.out.println("当前线程结束。。。" + 1);
//            return i;
//        }, executors);
//        System.out.println("异步任务执行结果-->" + future01.get());

        /**
         * 异步任务执行完成后的动作
         * 不以async结尾意味着,两个action使用相同的线程
         */
//        CompletableFuture.supplyAsync(() -> {
//            System.out.println("当前线程开始。。。" + Thread.currentThread().getId());
//            int i = 10 / 4;
//            System.out.println("当前线程结束。。。" + i);
//            return i;
//        },executors).whenComplete((res,e)->{
//            System.out.println("结果是" + res);
//            System.out.println("异常是" + e);
//        });
//        CompletableFuture.supplyAsync(() -> {
//            System.out.println("当前线程开始。。。" + Thread.currentThread().getId());
//            int i = 10 / 4;
//            System.out.println("当前线程结束。。。" + i);
//            return i;
//        },executors).whenCompleteAsync((res,e)->{
//            System.out.println("结果是" + res);
//            System.out.println("异常是" + e);
//        });
        //出现异常时
//        CompletableFuture<Integer> exceptionFuture = CompletableFuture.supplyAsync(() -> {
//            System.out.println("当前线程开始。。。" + Thread.currentThread().getId());
//            int i = 10 / 0;
//            System.out.println("当前线程结束。。。" + i);
//            return i;
//        }, executors).whenComplete((res, th) -> {
//            //虽然能得到异常信息，但没有修改
//            System.out.println("结果是" + res);
//            System.out.println("异常是" + th);
//        }).exceptionally(throwable -> {
//            //出现异常时默认的返回值
//            return 10;
//        });

        //使用handle既能感知结果，又能处理异常
//        CompletableFuture<Integer> handleFuture = CompletableFuture.supplyAsync(() -> {
//            System.out.println("当前线程开始。。。" + Thread.currentThread().getId());
//            int i = 10 / 5;
//            System.out.println("当前线程结束。。。" + i);
//            return i;
//        }, executors).handle((res,th) -> {
//           if(res != null){
//               return res  * 2;
//           }
//           if(null != th){
//               return 0;
//           }
//           return 10;
//        });

        /**
         * 异步编排
         * A、B异步任务串行化执行
         * A、B异步任务组合都执行完成
         * A、B步任务其中有一个执行完成
         * A、B、C同时执行完成
         * A、B、C其中有一个执行完成
         */
        //A、B异步任务串行化执行
        //TODO,thenRunAsync任务1执行完了之后执行任务2,不接受任务1的返回值，任务2也没有返回值
//        CompletableFuture.supplyAsync(() -> {
//            System.out.println("当前线程开始。。。" + Thread.currentThread().getId());
//            int i = 10 / 5;
//            System.out.println("当前线程结束。。。" + i);
//            return i;
//        }, executors).thenRunAsync(() -> {
//            System.out.println("任务2开始执行了...");
//        },executors);
        //TODO,任务1执行完了之后执行任务2,需要接受任务1的返回值，任务2没有返回值
//        CompletableFuture.supplyAsync(() -> {
//            System.out.println("当前线程开始。。。" + Thread.currentThread().getId());
//            int i = 10 / 5;
//            System.out.println("当前线程结束。。。" + i);
//            return i;
//        }, executors).thenAcceptAsync((res) -> {
//            System.out.println("任务1的执行结果为..." + res);
//        },executors);
        //TODO,任务1执行完了之后执行任务2,需要接受任务1的返回值，任务2有自己的返回值
//        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
//            System.out.println("当前线程开始。。。" + Thread.currentThread().getId());
//            int i = 10 / 5;
//            System.out.println("当前线程结束。。。" + i);
//            return i;
//        }, executors).thenApplyAsync((res) -> {
//            System.out.println("任务1的执行结果为..." + res);
//            return res * 2;
//        }, executors);

        /**
         *  A、B异步任务组合都执行完成
         */
        //TODO,任务1，2都执行完,然后执行任务3，,不需要接收任务1,2，的返回值
//        CompletableFuture<Integer> future01 = CompletableFuture.supplyAsync(() -> {
//            System.out.println("线程1开始。。。" + Thread.currentThread().getId());
//            int i = 10 / 5;
//            System.out.println("线程1结束。。。" + i);
//            return i;
//        }, executors);
//
//        CompletableFuture<Integer> futrue02 = CompletableFuture.supplyAsync(() -> {
//            System.out.println("当前线程2开始。。。" + Thread.currentThread().getId());
//            int i = 10 / 2;
//            System.out.println("当前线程2结束。。。" + i);
//            return i;
//        }, executors);
//
//        CompletableFuture<Void> future = future01.runAfterBothAsync(futrue02, () -> {
//            System.out.println("任务1--2都执行完了，开始执行任务3---");
//        }, executors);

        //TODO,任务1，2都执行完,然后执行任务3，,需要接收任务1,2的返回值
//        CompletableFuture<Integer> future01 = CompletableFuture.supplyAsync(() -> {
//            System.out.println("线程1开始。。。" + Thread.currentThread().getId());
//            int i = 10 / 5;
//            System.out.println("线程1结束。。。" + i);
//            return i;
//        }, executors);
//
//        CompletableFuture<Integer> futrue02 = CompletableFuture.supplyAsync(() -> {
//            System.out.println("当前线程2开始。。。" + Thread.currentThread().getId());
//            int i = 10 / 2;
//            System.out.println("当前线程2结束。。。" + i);
//            return i;
//        }, executors);
//
//        CompletableFuture<Void> future = future01.thenAcceptBothAsync(futrue02, (v1, v2) -> {
//            System.out.println("任务1--2都执行完了结果分别为 " + v1 + "++++" + v2 + "，开始执行任务3---");
//        }, executors);

        //TODO,任务1，2都执行完,然后执行任务3，,需要接收任务1,2的返回值,同时任务3也有返回值
//        CompletableFuture<Integer> future01 = CompletableFuture.supplyAsync(() -> {
//            System.out.println("线程1开始。。。" + Thread.currentThread().getId());
//            int i = 10 / 5;
//            System.out.println("线程1结束。。。" + i);
//            return i;
//        }, executors);
//
//        CompletableFuture<Integer> futrue02 = CompletableFuture.supplyAsync(() -> {
//            System.out.println("当前线程2开始。。。" + Thread.currentThread().getId());
//            int i = 10 / 2;
//            System.out.println("当前线程2结束。。。" + i);
//            return i;
//        }, executors);
//
//        CompletableFuture<Integer> future = future01.thenCombineAsync(futrue02, (v1, v2) -> {
//            System.out.println("任务1--2都执行完了结果分别为 " + v1 + v2 + "，开始执行任务3---");
//            return v1 * v2 * 2;
//        }, executors);

        //TODO,任务1，2其中一个执行完,然后执行任务3，,需要接收任务1,2的返回值,同时任务3也有返回值
//        CompletableFuture<Integer> future01 = CompletableFuture.supplyAsync(() -> {
//            System.out.println("线程1开始。。。" + Thread.currentThread().getId());
//            int i = 10 / 5;
//            System.out.println("线程1结束。。。" + i);
//            return i;
//        }, executors);
//
//        CompletableFuture<Integer> futrue02 = CompletableFuture.supplyAsync(() -> {
//            System.out.println("当前线程2开始。。。" + Thread.currentThread().getId());
//            int i = 10 / 2;
//            try {
//                Thread.sleep(3000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            System.out.println("当前线程2结束。。。" + i);
//            return i;
//        }, executors);

//        CompletableFuture<Void> future = future01.runAfterEitherAsync(futrue02, () -> {
//            System.out.println("任务1--2都执行完了结果分别为开始执行任务3---");
//        }, executors);

        //TODO,任务1，2其中一个执行完,然后执行任务3，,需要接收任务1,2中的返回值,3也有返回值
//        future01.acceptEitherAsync(futrue02, (res) -> {
//            System.out.println("任务1--2有一个执行完了---" + res);
//        }, executors);

//        CompletableFuture<Integer> future = future01.applyToEitherAsync(futrue02, (res) -> {
//            System.out.println("任务1--2有一个执行完了---" + res);
//            return 100;
//        }, executors);

        CompletableFuture<String> future01 = CompletableFuture.supplyAsync(() -> {
            return "图片";
        }, executors);

        CompletableFuture<String> future02 = CompletableFuture.supplyAsync(() -> {
            return "ces";
        }, executors);

        CompletableFuture<String> future03 = CompletableFuture.supplyAsync(() -> {
            return "图dad片";
        }, executors);

        CompletableFuture<Void> future = CompletableFuture.allOf(future01, future02, future03);
        future.get();

        System.out.println("---------------main方法结束----------结果为-->" + future01.get() +"++"+future02.get() +"++"+ future03.get());
    }

    /**
     * 创建线程的4种方式
     * 1.继承Thread,重写run方法
     */
    public static class Thread01 extends Thread{
        @Override
        public void run() {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("继承Threa类，重写run方法");
        }
    }
    //实现Runnable接口
    public static class Thread02 implements Runnable{
        @Override
        public void run() {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("实现Runnable接口的run方法");
        }
    }
    //实现Callable接口
    public static class Thread03 implements Callable<Integer>{
        @Override
        public Integer call() {
//            try {
//                Thread.sleep(2000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
            return 10 / 4;
        }
    }
}
