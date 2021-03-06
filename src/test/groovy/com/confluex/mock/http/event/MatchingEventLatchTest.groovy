package com.confluex.mock.http.event

import com.confluex.mock.http.ClientRequest
import com.confluex.mock.http.matchers.HttpMatchers
import org.junit.Test

import static org.mockito.Mockito.*

class MatchingEventLatchTest {
    MatchingEventLatch latch

    @Test
    void awaitShouldBlockUntilAllEventsOccur() {
        latch = new MatchingEventLatch(HttpMatchers.anyRequest(), 2)
        boolean finished = false
        Thread.start {
            finished = latch.await(1000)
        }
        assert ! finished
        latch.addEvent(mock(ClientRequest))
        Thread.sleep(200)
        assert ! finished
        latch.addEvent(mock(ClientRequest))
        Thread.sleep(200)
        assert finished
    }

    @Test
    void latchShouldWaitForMatchingEvents() {
        latch = new MatchingEventLatch(HttpMatchers.path('/special'))
        boolean finished = false
        Thread.start {
            finished = latch.await(1000)
        }

        assert ! finished

        ClientRequest request = mock(ClientRequest)
        when(request.getPath()).thenReturn('/ordinary')

        latch.addEvent(request)
        Thread.sleep(200)

        assert ! finished

        request = mock(ClientRequest)
        when(request.getPath()).thenReturn('/special')

        latch.addEvent(request)
        Thread.sleep(200)

        assert finished
    }
}
