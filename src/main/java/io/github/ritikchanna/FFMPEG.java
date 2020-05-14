package io.github.ritikchanna;

import java.io.*;

public class FFMPEG {

    public static void execute(String command) throws Exception {
        Runtime.getRuntime().exec(ExportResource("/ffmpeg.exe")+" "+command.trim());
    }


    static public String ExportResource(String resourceName) throws Exception {
        InputStream stream = null;
        OutputStream resStreamOut = null;
        String jarFolder;
        try {
            stream = FFMPEG.class.getResourceAsStream(resourceName);//note that each / is a directory down in the "jar tree" been the jar the root of the tree
            if(stream == null) {
                throw new Exception("Cannot get resource \"" + resourceName + "\" from Jar file.");
            }

            int readBytes;
            byte[] buffer = new byte[4096];
            jarFolder = new File(FFMPEG.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()).getParentFile().getPath().replace('\\', '/');
            resStreamOut = new FileOutputStream(jarFolder + resourceName);
            while ((readBytes = stream.read(buffer)) > 0) {
                resStreamOut.write(buffer, 0, readBytes);
            }
        } catch (Exception ex) {
            throw ex;
        } finally {
            stream.close();
            resStreamOut.close();
        }

        return jarFolder + resourceName;
    }
}
