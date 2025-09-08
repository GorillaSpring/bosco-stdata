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

import com.bosco.stdata.model.ImportChanges;
import com.bosco.stdata.model.ImportSetting;


public  class ImportHelper {

    public static Boolean importRunning = false;

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


    public static Boolean CheckTooManyChanges (ImportChanges impChanges, double maxAllowedChangesPercent) {
        // return true if too many changes.

        // maxAllowedChangesPercent is   0.1 for 10%,   0.2 for 20% etc.

         double percentDiff = Math.abs(impChanges.baseStudentCount - impChanges.importStudentCount) / 
                (
                    (impChanges.baseStudentCount + impChanges.importStudentCount) / 2.0
                )

                ;

            if (percentDiff > maxAllowedChangesPercent)
                return true;

            

            // so 23 * 0.1 = 2.3   => 2 > 0
            // should this be less?


            if ((int)(impChanges.importStudentCount * maxAllowedChangesPercent) < impChanges.importStudentChanged) {
                // the sutdents changed is greater then 10% so bail.

                return true;
            }

            return false;
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

        

        System.out.println("Checking Import Files");
        for (String fileName : fileNames) {

            

            Path filePath = Paths.get(baseFolder + fileName);
            System.out.println("  -- Checking: " + filePath.toString());
            if (!Files.exists(filePath)) {
                System.out.println("   ------ DID NOT FIND");
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
            System.out.println("  ---   ERROR lenght of col headers is different --- ");
            return false;
        }
        for (int i = 0; i < headerRow.length; i++) {
            // System.out.println("Checking : " +  headerRow[i] + " = " + headers[i]);


            // Try this and see.

            // This seems to be working fine.
            // clean up and see how we do.



            byte[] utf8Bytes = headerRow[i].trim().getBytes(StandardCharsets.ISO_8859_1);

            String headerRowUtf8 = new String(utf8Bytes, StandardCharsets.UTF_8);

            String trimmedHeaderRow = headerRowUtf8.replace("?", ""); //.replace("_", "");

            // EntryIEP_Date

            // String thr = trimmedHeaderRow.replace("_", "");

            // String th = headers[i].trim().replace("_", "");



                 

          
            int res = trimmedHeaderRow.compareToIgnoreCase(headers[i]);
            //int res = thr.compareToIgnoreCase(th);

            // if res 
            // if (res != 0) {
            //     // just try the old way.
            //     res = headerRow[i].trim().compareToIgnoreCase(headers[i].trim());
            // }

            if (res != 0) {
                 System.out.println("   ---- Error in Clo headers - [" + trimmedHeaderRow + "] : []" + headers[i] + "]");
                return false;
            }

            // if (!trimmedHeaderRow.equalsIgnoreCase(headers[i].replace("_", ""))) {
            //     System.out.println("   ---- Error in Clo headers - " + trimmedHeaderRow + " : " + headers[i]);
            //     return false;
            // }


        }
        return true;
    }


}
