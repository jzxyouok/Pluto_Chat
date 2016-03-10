package com.wl.pluto.plutochat.eventbus;

/**
 * *EventBus**
 * 是一个Android端优化的publish/subscribe消息总线，简化了应用程序内各组件间、组件与后台线程间的通信。
 * 比如请求网络，等网络返回时通过Handler或Broadcast通知UI，两个Fragment之间需要通过Listener通信，
 * 这些需求都可以通过**EventBus**实现。
 * <p>
 * <p>
 * <p>
 * <p>
 * 作为一个消息总线，有三个主要的元素：
 * 1: Event：事件
 * 2: Subscriber：事件订阅者，接收特定的事件
 * 3: Publisher:事件发布者，用于通知Subscriber有事件发生
 * <p>
 * <p>
 * <p>
 * <p>
 * 1: Event 可以是任意类型的对象。
 * 2: Subscriber 在EventBus中，使用约定来指定事件订阅者以简化使用。即所有事件订阅都都是以onEvent开头的函数，
 * 具体来说，onEventPostThread，onEventMainThread，onEventBackgroundThread，onEventAsync这四个，这个和ThreadMode有关，后面再说。
 * 3: Publisher 可以在任意线程任意位置发送事件，直接调用EventBus的`post(Object)`方法，可以自己实例化EventBus对象，但一般使用默认的单例就好了：
 * EventBus.getDefault()`，根据post函数参数的类型，会自动调用订阅相应类型事件的函数。
 * <p>
 * <p>
 * <p>
 * ThreadMode
 * 前面说了，Subscriber函数的名字只能是那4个，因为每个事件订阅函数都是和一个`ThreadMode`相关联的，ThreadMode指定了会调用的函数。有以下四个ThreadMode：
 * 1: PostThread：事件的处理在和事件的发送在相同的进程，所以事件处理时间不应太长，不然影响事件的发送线程，而这个线程可能是UI线程。对应的函数名是onEventPostThread。
 * 2: MainThread: 事件的处理会在UI线程中执行。事件处理时间不能太长，这个不用说的，长了会ANR的，对应的函数名是onEventMainThread。
 * 3: BackgroundThread：事件的处理会在一个后台线程中执行，对应的函数名是onEventBackgroundThread，虽然名字是BackgroundThread，事件处理是在后台线程，
 * 但事件处理时间还是不应该太长，因为如果发送事件的线程是后台线程，会直接执行事件，如果当前线程是UI线程，事件会被加到一个队列中，由一个线程依次处理这些事件，
 * 如果某个事件处理时间太长，会阻塞后面的事件的派发或处理。
 * 4: Async：事件处理会在单独的线程中执行，主要用于在后台线程中执行耗时操作，每个事件会开启一个线程（有线程池），但最好限制线程的数目。
 * <p>
 * <p>
 * 根据事件订阅都函数名称的不同，会使用不同的ThreadMode，比如果在后台线程加载了数据想在UI线程显示，订阅者只需把函数命名为onEventMainThread。
 * <p>
 * <p>
 * <p>
 * <p>
 * 简单使用
 * <p>
 * 基本的使用步骤就是如下4步，点击此链接查看例子及介绍。
 * 1: 定义事件类型：public class MyEvent {}`
 * 2: 定义事件处理方法：public void onEventMainThread 等那四个onEvent开头的方法
 * 3: 注册订阅者：EventBus.getDefault().register(this)`
 * 4: 发送事件：EventBus.getDefault().post(new MyEvent())`
 * <p>
 * <p>
 * <p>
 * <p>
 * <p>
 * Created by pluto on 15-11-4.
 */
public class GestureEvent extends BaseEvent{

    private int touchDownX;
    private int touchUpX;

    public GestureEvent(int downX, int upX) {
        this.touchDownX = downX;
        this.touchUpX = upX;
    }

    public int getTouchDownX() {
        return touchDownX;
    }

    public int getTouchUpX() {
        return touchUpX;
    }
}
