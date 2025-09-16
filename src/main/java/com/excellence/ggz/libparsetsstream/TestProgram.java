package com.excellence.ggz.libparsetsstream;

import com.excellence.ggz.libparsetsstream.Interface.ParesTsStream;
import com.excellence.ggz.libparsetsstream.Logger.LoggerManager;

/**
 * @author ggz
 */
public class TestProgram {
    private static final String INPUT_FILE1_PATH = "/Users/miaoyun/Documents/workspace/001.ts";
    private static final String INPUT_FILE2_PATH = "f:/bak/sx/tools/001.ts";
    private static final String TAG = TestProgram.class.getName();

    public static void main(String[] args) {
        ParesTsStream paresTsStream = new DvbManager();
        String json = paresTsStream.parseTsStreamByFile(INPUT_FILE1_PATH);

        LoggerManager logger = LoggerManager.getInstance();
        logger.debug(TAG, json);
    }

//    public static void main(String[] args) throws InterruptedException {
//        LoggerManager logger = LoggerManager.getInstance();
//
//        Flow.Subscriber<String> sub1 = new Flow.Subscriber<String>() {
//            private Flow.Subscription mSubscription;
//
//            @Override
//            public void onSubscribe(Flow.Subscription subscription) {
//                mSubscription = subscription;
//                mSubscription.request(1);
//            }
//
//            @Override
//            public void onNext(String item) {
//                logger.debug("sub1", "sub1: onNext " + item);
//                try {
//                    // 模拟消费者处理数据耗时
//                    Thread.sleep(100);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                mSubscription.request(1);
//            }
//
//            @Override
//            public void onError(Throwable throwable) {
//            }
//
//            @Override
//            public void onComplete() {
//                logger.debug("sub1", "sub1: onComplete");
//            }
//        };
//
//        Flow.Subscriber<String> sub2 = new Flow.Subscriber<String>() {
//            private Flow.Subscription mSubscription;
//
//            @Override
//            public void onSubscribe(Flow.Subscription subscription) {
//                mSubscription = subscription;
//                mSubscription.request(1);
//            }
//
//            @Override
//            public void onNext(String item) {
//                logger.debug("sub2", "sub2: onNext " + item);
//                mSubscription.request(1);
//            }
//
//            @Override
//            public void onError(Throwable throwable) {
//            }
//
//            @Override
//            public void onComplete() {
//                logger.debug("sub2", "sub2: onComplete");
//            }
//        };
//
//        SubmissionPublisher<String> publisher = new SubmissionPublisher<>();
//        publisher.subscribe(sub1);
//        publisher.subscribe(sub2);
//
//        for (int i = 0; i < 100; i++) {
//            try {
//                // 模拟生产者生成数据耗时
//                Thread.sleep(10);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            logger.debug("publisher", "submit: 数据" + i);
//            publisher.submit("数据" + i);
//        }
//        publisher.close();
//    }
}