package com.example.android.filmesfamosos;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.example.android.filmesfamosos.model.Movie;
import com.example.android.filmesfamosos.model.Review;
import com.example.android.filmesfamosos.model.Title;
import com.example.android.filmesfamosos.utilities.App;
import com.example.android.filmesfamosos.utilities.MovieDatabaseUtils;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.example.android.filmesfamosos", appContext.getPackageName());
    }

    @Test
    public void database_insert() throws Exception{
        Movie dummyMovie = new Movie.MovieBuilder(1,
                new Title("Test", "Test"),
                "").build();
        Review dummyReview = new Review("1","Me","Test","");
        MovieDatabaseUtils.insertMovie(dummyMovie, InstrumentationRegistry.getTargetContext());
    }
}
