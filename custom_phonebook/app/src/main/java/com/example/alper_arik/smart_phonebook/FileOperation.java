package com.example.alper_arik.smart_phonebook;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

    public class FileOperation {

        public FileOperation() {
        }


        public static void appendToFile(String fileName, String data, Context context) {
            try {
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(fileName, Context.MODE_APPEND));
                outputStreamWriter.append(data + "#");
                outputStreamWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public static String readFromFile(String fileName, Context context) {

            String ret = "";

            try {
                InputStream inputStream = context.openFileInput(fileName);

                if (inputStream != null) {
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    String receiveString = "";
                    StringBuilder stringBuilder = new StringBuilder();

                    while ((receiveString = bufferedReader.readLine()) != null) {
                        stringBuilder.append(receiveString);
                    }

                    inputStream.close();
                    ret = stringBuilder.toString();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return ret;
        }

        public static void clearFile(String fileName, Context context) {
            try {
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(fileName, Context.MODE_PRIVATE));
                outputStreamWriter.write("");
                outputStreamWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }