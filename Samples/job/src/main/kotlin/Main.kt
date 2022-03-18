import kotlinx.coroutines.*

suspend fun main(args: Array<String>) {
//    coroutineScope {
//        val job = launch { // launch a new coroutine and keep a reference to its Job
//            delay(1000L)
//            println("World!")
//        }
//        println("Hello")
//        job.join() // wait until child coroutine completes
//        println("Done")
//    }

    coroutineScope {
        val job = launch {
            repeat(1000) { i ->
                println("job: I'm sleeping $i ...")
                delay(500L)
            }
        }
        delay(1300L) // delay a bit
        println("main: I'm tired of waiting!")
        job.cancel() // cancels the job
        job.join() // waits for job's completion
        println("main: Now I can quit.")
    }
}
