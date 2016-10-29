package van.srt;

import static org.testng.Assert.*;

/**
 * &copy; fanhuagang@gmail.com
 * Created by van on 29/10/2016.
 */
public class SrtTimeTest {
    @org.testng.annotations.BeforeMethod
    public void setUp() throws Exception {

    }

    @org.testng.annotations.AfterMethod
    public void tearDown() throws Exception {

    }

    @org.testng.annotations.Test
    public void testGetSrtTime() throws Exception {
        String s = "00:06:10,329 --> 00:06:11,788";
        String srtTime = new SrtTime(s).getSrtTime();

        assertEquals(srtTime, s);
    }

}