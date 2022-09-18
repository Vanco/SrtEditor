package io.vanstudio.srt;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GTTest {

    @Test
    void translateText() {
        System.out.println("-----test-----");
        String txt="Happiness is to live in the moment and be happy with each other. Sweet thoughts linger day and night, belong to each other, have a clear and elegant taste, and are free and unfettered between mountains and rivers and clouds.";
        Translator g = Translator.getInstance();
        try {
            System.out.println( g.translateText(txt,"en","zh_cn"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        //System.out.println( g.translateText("谁能说支持不支持","auto","en"));
        assertTrue(true);
    }
}