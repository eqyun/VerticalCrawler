package test;

//import com.pada.spider.tool.SpiderSynchronizer;
import org.junit.Test;

/**
 * Created by eqyun on 2014/11/4.
 */
public class SynTest {

   // SpiderSynchronizer synchronizer = new SpiderSynchronizer();

    @Test
    public void test() throws InterruptedException {


        MyThread myThread = new MyThread();
        MyThread myThread2 = new MyThread();
        MyThread myThread3 = new MyThread();
        MyThread myThread4 = new MyThread();
        myThread.start();
        myThread2.start();
        myThread2.join();
        myThread3.start();
        myThread3.join();

        myThread4.start();



    }
    class MyThread extends Thread{

        @Override
        public void run() {
            System.out.println(Thread.currentThread().getId());
        }
    }
}
