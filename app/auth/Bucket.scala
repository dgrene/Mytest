package auth

/**
 * @author deepak
 */

import java.util.concurrent.{ TimeUnit, SynchronousQueue }
import scala.collection.mutable.Queue

class Bucket(rate: Int, unit: TimeUnit, user: User) {
  // Precalculate the sleep time.
  private val millis = unit.toMillis(1) / rate

  private val nanos = TimeUnit.MILLISECONDS.toNanos(unit.toMillis(1) % rate) / rate

  //println("nanos :: " + nanos)
  //println("millies :: " + millis)

  //creating queue for user
  private val queue = new Queue[Int]

  def fullBucket(rate: Int) {
    for (i <- 1 to rate) {
      queue.enqueue(1)
    }
    //println("bucket Full :: " + queue.length)
  }

  // Background thread to generate drops.
  private val filler = new Thread(new Runnable() {
    def run() {
      println("length :: " + queue.length)
      while (true) {
        println("THREAD IS RUNNING ....")
        //println("RATE :: "+rate)
        //println("Queue Len :: "+queue.length)
        if (queue.length < rate) {
          Thread.sleep(millis, nanos.toInt)
          queue.enqueue(1)
          println("new Drop")
          println(s"unit remaining :: ${queue.length}")
        }
        // if (queue.length == rate) {

        // }
      }
    }
  }, "tokenbucket-filler")
  fullBucket(rate)
  filler.setDaemon(true)
  filler.start()

  /**
   * Wait for a drop indefinitely.
   */
  def take(): (Boolean, String) = {
    val timeLeft = (millis * ((if (queue.length == 0) rate else rate - queue.length)) / 1000).toString() + " sec"
    println("unit :: " + queue.length)
    println("takinig")
    //println(queue.take())
    println(s"unit remaining :: ${queue.length - 1}")
    if (user.currentStatus == false) {
      if (queue.length == rate) {
        println("Serve again :: ")
        user.currentStatus = true
        queue.dequeue()
        (true, "")
      } else {
        //hold it
        println("Holding ...")
        (false, timeLeft)
      }
    } else {
      if (queue.isEmpty) {
        println("wait")
        //Thread.sleep(10000)

        user.currentStatus = false
        (false, timeLeft)
      } else {

        queue.dequeue()
        //println("success")
        //println(s"unit remaining :: ${queue.length - 1}")

        (true, "")
      }
    }

  }
}

