package com.bosco.stdata.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Stream;

import com.bosco.stdata.model.ImportSetting;


public  class ImportHelper {

    public static String ValueForSetting(List<ImportSetting> inputSettings, String setting)  throws Exception{

         ImportSetting is = inputSettings.stream()
            .filter(s -> s.getSetting().equals(setting))
            .findFirst()
            .orElse(null);

        if (is == null) {
            // this is an exeption.
            throw new IOException ("Could not find input setting of : " + setting);
        }
        else {
            return is.value;
        }

    }

    private static void moveFile (final Path file, Path targetDirectory) throws IOException {
        
            if (Files.isRegularFile(file)) {
                    Path targetFile = targetDirectory.resolve(file.getFileName());
                    try {
                        Files.move(file, targetFile, StandardCopyOption.REPLACE_EXISTING);
                    }
                    catch (IOException e) {
                        System.out.println(e.getMessage());
                        throw e;
                        
                    }
                }
        
    }

    public static void MoveFiles (String sourcePath, String targetPath) throws Exception {

        
        String dateFolder = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss"));

        Path sourceDirectory = Paths.get (sourcePath); 
        Path targetDirectory = Paths.get(targetPath + dateFolder); 

        if (!Files.exists(targetDirectory)) {
            Files.createDirectories(targetDirectory);
        }

        

         try (Stream<Path> files = Files.list(sourceDirectory)) {
            
            files.forEach(file ->  {

                
                if (Files.isRegularFile(file)) {
                    Path targetFile = targetDirectory.resolve(file.getFileName());
                    try {
                        Files.move(file, targetFile, StandardCopyOption.REPLACE_EXISTING);
                    }
                    catch (IOException e) {
                        System.out.println(e.getMessage());
                        //throw e;
                        
                    }
                }
            });
        }
        catch (IOException e) {
            throw e;
        }


    }


    public static Boolean CheckFilesExist (String baseFolder, String[] fileNames)  {

        

        for (String fileName : fileNames) {
            Path filePath = Paths.get(baseFolder + fileName);
            if (!Files.exists(filePath)) {
                return false;
            }
        }


        return true;
    }


    private static String convertFromUtf8ToIso(String s1) {
        if(s1 == null) {
            return null;
        }
        String s = new String(s1.getBytes(StandardCharsets.UTF_8));
        byte[] b = s.getBytes(StandardCharsets.ISO_8859_1);
        return new String(b, StandardCharsets.ISO_8859_1);
    }

    public static Boolean CheckColumnHeaders (String[] headerRow, String[] headers) {


        if (headerRow.length != headers.length) {
            return false;
        }
        for (int i = 0; i < headerRow.length; i++) {
            // System.out.println("Checking : " +  headerRow[i] + " = " + headers[i]);


            // Try this and see.

            // This seems to be working fine.
            // clean up and see how we do.



            byte[] utf8Bytes = headerRow[i].getBytes(StandardCharsets.ISO_8859_1);

            String s8 = new String(utf8Bytes, StandardCharsets.UTF_8);

                // defaultBytes = headerRow[i].getBytes();
                // sd = new String(defaultBytes, StandardCharsets.UTF_8);
          



            String t8 = s8.replace("?", "");
            //String td = sd.replace("?", "");

            //System.out.println("8:" + s8 + " d:" + sd);
            //System.out.println("t8:" + t8 + " td:" + td);

            //System.out.println("HERE");

            if (!t8.equalsIgnoreCase(headers[i])) {
                return false;
            }

            // This one works.
            // if (!headerRow[i].contains(headers[i])) {
            //      return false;
            // }
            

            // System.out.println("Checking : " +  headerRow[i] + " = " + headers[i]);
            // String hrx = headerRow[i];
            // String h = headers[i];



            //  String hr =  convertFromUtf8ToIso(headerRow[i]);
            // // String h = convertFromUtf8ToIso(headers[i]);


            //  System.out.println("Checking : " +  hr + " = " + h);

            //  if (!hr.equalsIgnoreCase(h)) {
            //      return false;
            // }
            


            // String hr =  new String(headerRow[i].getBytes(Charset.forName("utf-8")));
            // String h = new String(headers[i].getBytes(Charset.forName("utf-8")));
          
            // byte[] ansiBytes = headerRow[i].getBytes(StandardCharsets.ISO_8859_1); // Or Charset.forName("Cp1252")
   
            // String hr = new String(ansiBytes, StandardCharsets.ISO_8859_1); // Or Charset.forName("Cp1252")
         
            
            //ansiBytes = headers[i].getBytes(StandardCharsets.ISO_8859_1); // Or Charset.forName("Cp1252")
   
            //String h = new String(ansiBytes, StandardCharsets.ISO_8859_1); // Or Charset.forName("Cp1252")
            //String h = headers[i];

            //String h = new String(headerRow[i], );
         

            //  System.out.println("Checking : " +  headerRow[i] + " = " + headers[i]);
            // // System.out.println("Checking : " +  hr + " = " + h);

            // // if (!hr.equalsIgnoreCase(h)) {
            // //     return false;
            // // }
        

            // if (!headerRow[i].equalsIgnoreCase(headers[i])) {
            //      return false;
            // }
            


        }
        return true;
    }


}
