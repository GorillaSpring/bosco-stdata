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
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

import org.apache.commons.text.WordUtils;

import com.bosco.stdata.model.ImportSetting;

import java.text.ParseException;


public  class ImportHelper {

    public static Boolean importRunning = false;

    
    // for debug output.

    public static int debuggerCountdown = 1;

    public static void DebugCountdownSet(int count) {
        debuggerCountdown = count;

        // we want to pad this 
        String paddedString = String.format("%08d", debuggerCountdown);

        System.out.print(paddedString);
    }

    public static void DebugCountdown() {
        debuggerCountdown--;
        String paddedString = String.format("%08d", debuggerCountdown);

        System.out.print("\b\b\b\b\b\b\b\b");

        System.out.print(paddedString);
    }



    public static int debugSpinner = 1;

    public static void DebugSpin(Boolean start) {

        if (!start) {
            System.out.print("\b");
        }
        switch (debugSpinner) {
            case 1:
                System.out.print("|");
                break;
            case 2:
                System.out.print("/");
                break;
            case 3:
                System.out.print("-");
                break;
            case 4:
                System.out.print("\\");
                break;
            case 5:
                System.out.print("*");
                break;
            default:
                System.out.print(".");
                debugSpinner = 1;
                break;
        }

        debugSpinner++;

    }


    public static String TitleCase(String str) {
        return WordUtils.capitalizeFully(str, '\'', '-', ' ');
    }
  


    public static String GradeFromGradeCode (String gradeCode) {
        String grade = 
        switch(gradeCode) {
            case "PS" -> "PK";   // This is from Bosco Power School   May be PK
            case "KG" -> "K";   // This is from Bosco Power School   May be PK
            case "-1" -> "EC";
            case "-2" -> "PK";
            case "0" -> "K";
            default -> gradeCode;

        };

        return grade;
    }

    public static String GenderFromSex (String sex) {
        String sexLower = sex.toLowerCase();
        if (sexLower.startsWith("m"))
            return "M";
        if (sexLower.startsWith("f"))
            return "F";
        return "O";
    }


    public static String DateToStdFormat (String dateString) throws Exception {
        // this will convert a date string to yyyy-MM-dd if it is in one of the provide formats
        // it will thoww an exception if the data is invalid.

        String[] possibleFormats = {
            "MM-dd-yy",
            "MM-dd-yyyy",
            "yyyy-MM-dd HH:mm:ss",
            "yyyy-MM-dd",
            "MM/dd/yyyy HH:mm:ss",
            "MM/dd/yyyy",
            "MM/dd/yy"
            
        };


        Date parsedDate = null;

        for (String format : possibleFormats) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(format);
                sdf.setLenient(false); // Important for strict parsing
                parsedDate = sdf.parse(dateString);
                //System.out.println("Parsed '" + dateString + "' with format '" + format + "': " + parsedDate);
                break; // Exit loop if parsing is successful
            } catch (ParseException e) {
                // Try next format
            }
        }

        if (parsedDate == null) {
            throw new Exception("Could not parse date : "+ dateString);
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(parsedDate);




        // DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
            
        //     "[yyyy-MM-dd]" +
            
        //     "[MM/dd/yyyy]"
        // );

        // LocalDate parsedDate = LocalDate.parse(date, formatter);

        //  SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        // return sdf.format(parsedDate);



    }

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


    

    public static Boolean isUnix () {

        Boolean isUnix = true;

        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("win")) {
            isUnix = false;
        }
        // } else if (osName.contains("linux") || osName.contains("nix") || osName.contains("nux")) {
        //     System.out.println("Running on Linux/Unix");
        // } else if (osName.contains("mac")) {
        //     System.out.println("Running on macOS");
        // } else {
        //     System.out.println("Unknown OS: " + osName);
        // }
        return isUnix;
    }


     public static String getLastStringInPath(String path) {
        Path p = Paths.get(path);
        Path fileName = p.getFileName();
        // getFileName() can return null or an empty path for edge cases, 
        // convert to string for consistent result.
        return (fileName != null) ? fileName.toString() : ""; 
    }

    public static void MoveFiles (String sourcePath, String targetPath) throws Exception {

        if (targetPath.isEmpty())
            return;


        try {
        
            Boolean isUnix = isUnix();

            

            

            if (isUnix) {
                System.out.println("Running on Unix");
            }
            else {
                System.out.println("Running on Windows");
            }



            String dateFolder = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss"));

            Path sourceDirectory = Paths.get (sourcePath); 
            Path targetDirectory = Paths.get(targetPath + "/" + dateFolder); 

            // so, first, we will check if the targetPath exists.
            // /var/sftp/data/archive/DISTRICT




            if (!Files.exists(Paths.get(targetPath))) {
                // mkdir + targetPath
                if (isUnix) {

                    System.out.println("UNIX: Creating Dirctory for " + targetPath) ;
                    String[] cmd2 = {"sudo", "mkdir", targetPath};
                    Process process2 = Runtime.getRuntime().exec(cmd2);
                    int exitCode2 = process2.waitFor();
                }
                else {
                    System.out.println("Windows: Creating Dirctory for " + targetPath) ;
                    String[] cmd2 = {"cmd.exe", "/c", "mkdir", targetPath};
                    Process process2 = Runtime.getRuntime().exec(cmd2);
                    int exitCode2 = process2.waitFor();
                }

                System.out.println ("Dirctory Created: " + targetPath);

            }




            if (!Files.exists(targetDirectory)) {

                // so here we need to create the district in the folder if it does not exist.
                // the input should be /var/sftp/data/archive/DISTRICT/

                // if we split on the /

                System.out.println("Creating Archive Folder: " + targetDirectory.toString());
                // Files.createDirectories(targetDirectory);
                // System.out.println(" -- Created");

                if (isUnix) {

                    System.out.println("UNIX: Creating Time Stamp Dir " + dateFolder) ;
                    String[] cmd2 = {"sudo", "mkdir", targetDirectory.toString()};
                    Process process2 = Runtime.getRuntime().exec(cmd2);
                    int exitCode2 = process2.waitFor();
                }
                else {
                    System.out.println("Windows: Creating Time Stamp Dir " + dateFolder) ;
                    String[] cmd2 = {"cmd.exe", "/c", "mkdir", targetDirectory.toString()};
                    Process process2 = Runtime.getRuntime().exec(cmd2);
                    int exitCode2 = process2.waitFor();
                }


            }

            // // if isUnix, just add sudo.
            // // everthing else is GOOD on both.

            // if (isUnix) {
            //     //   think we want sudo mv source_directory/* destination_directory/

            //     //blevy@tstbk12:/var/sftp/data$ sudo mkdir /var/sftp/data/archive/testmv1
            //     //blevy@tstbk12:/var/sftp/data$ sudo mv /var/sftp/data/sftp_user/files/* /var/sftp/data/archive/testmv1



            //     String[] cmd2 = {"ls", "-l", "/path/to/directory"};
            //     Process process2 = Runtime.getRuntime().exec(cmd2);
            //     int exitCode2 = process2.waitFor();
            // }


        

            
            System.out.println("MOVING ALL FILES NOW ");

            if (isUnix) {

                    
                    String[] cmd2 = {"sudo", "mv", sourcePath + "/*", targetDirectory.toString()};
                    Process process2 = Runtime.getRuntime().exec(cmd2);
                    int exitCode2 = process2.waitFor();
                }
                else {
                    String[] cmd2 = {"cmd.exe", "/c", "move", sourcePath + "/*", targetDirectory.toString()};

                       try {
                            Process process = Runtime.getRuntime().exec(cmd2);
                            
                            // Remember to handle input/error streams and wait for process completion
                            // (see notes in the ProcessBuilder example)
                            process.waitFor();
                            
                        } catch (IOException | InterruptedException e) {
                            e.printStackTrace();
                        }


                    System.out.println("DONE");

                    // Process process2 = Runtime.getRuntime().exec(cmd2);
                    // int exitCode2 = process2.waitFor();

                    // System.out.println("Got code of : " + exitCode2);
                }

            //  try (Stream<Path> files = Files.list(sourceDirectory)) {
                
            //     files.forEach(file ->  {

            //         System.out.println(" - " + file.getFileName().toString());
                    
            //         if (Files.isRegularFile(file)) {
            //             Path targetFile = targetDirectory.resolve(file.getFileName());

            //             System.out.println("   -- Try Move : " + targetPath.toString());

            //             try {
            //                 Files.move(file, targetFile, StandardCopyOption.REPLACE_EXISTING);
            //                 System.out.println("     -----  Moved!");
            //             }
            //             catch (IOException e) {
            //                 System.out.println("   ---   Ecxeption moving file: ");
            //                 System.out.println(e.getMessage());
            //                 //throw e;
                            
            //             }
            //         }
            //         else {
            //             System.out.println ("    --  NOT a isRegularFile");
            //         }
            //     });
        }
        catch (IOException e) {
            System.out.println ("MoveFile - Exception : " + e.getMessage());
            throw e;
        }


    }


    public static Boolean CheckFilesExist (String baseFolder, String[] fileNames)  {

        

        System.out.println("Checking Import Files");
        for (String fileName : fileNames) {

            

            Path filePath = Paths.get(baseFolder + "/" + fileName);
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
