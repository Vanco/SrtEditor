package io.vanstudio.srt;

import javafx.scene.image.Image;

import java.net.URL;

public class ImageUtil {
    public static Image createImage(Object context, String resourceName) {
        URL _url = context.getClass().getResource(resourceName);
        assert _url != null;
        return new Image(_url.toExternalForm());
    }
}
