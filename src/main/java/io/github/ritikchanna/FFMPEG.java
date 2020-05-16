package io.github.ritikchanna;

import java.io.*;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class FFMPEG {
static String ffmpeg = null;

    static {
        try {
            ffmpeg = extractFFMPEG();
        }catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException("Unable to initialize FFMPEG "+e.getMessage());
        }
    }

    public static void execute(String command) throws Exception {
       if(ffmpeg==null)
           ffmpeg = extractFFMPEG();

        Runtime.getRuntime().exec(ffmpeg+"/"+command.trim());
    }


    private static String extractFFMPEG() throws IOException, InstantiationException {
        String name = System.getProperty("os.name").trim().toLowerCase();
        String arch = System.getProperty("os.arch").trim().toLowerCase();

        if(name.contains("windows")&&arch.contains("32")){
            return extractResource("win32");
        }
        else if(name.contains("windows")&&arch.contains("64")){
            return extractResource("win64");
        }
        else if(name.contains("mac")){
            return extractResource("mac");
        }
        else
            throw new InstantiationException(name+" : "+arch+" not supported.");

    }

    private static String extractResource(String os) throws IOException {
        final File jarFile = new File(FFMPEG.class.getProtectionDomain().getCodeSource().getLocation().getPath());
        String path;
        if(jarFile.isFile()) {  // Run with JAR file
            path = jarFile.getParent();
            final JarFile jar = new JarFile(jarFile);
            final Enumeration<JarEntry> entries = jar.entries(); //gives ALL entries in jar
            while(entries.hasMoreElements()) {
                final String name = entries.nextElement().getName();
                System.out.println(name);
                if (name.startsWith("bin/"+os + "/")) { //filter according to the path
                    copyFile(name,path);
                }
            }
            jar.close();
        } else { // Run with IDE
            final URL url = FFMPEG.class.getResource("/bin/" + os);
            path = url.getPath();
        }
        return path;

    }


    private static boolean copyFile(String srcFile, String destFolder) throws IOException {
        srcFile = srcFile.replace('\\','/');
        destFolder = destFolder.replace('\\','/');
        File directory = new File(destFolder);
        if (!directory.exists())
            directory.mkdir();
        srcFile = "/"+srcFile.substring(srcFile.lastIndexOf("bin/"));
        try(InputStream inputStream = FFMPEG.class.getResourceAsStream(srcFile);
            OutputStream outputStream = new FileOutputStream(destFolder+"/"+srcFile.substring(srcFile.lastIndexOf('/')+1));
        ) {
            int readBytes;
            byte[] buffer = new byte[4096];
            while ((readBytes = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, readBytes);
            }
        }catch (Exception e){
            return false;
        }
       return true;
    }
}
