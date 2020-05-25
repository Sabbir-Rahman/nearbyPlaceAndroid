package com.example.myapplication;

import android.net.Uri;
import android.renderscript.ScriptGroup;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class DownloadUrl
{
    public String ReadTheUrl(String placeURL) throws IOException
    {
        String Data = "";
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection = null;

        try
        {
            //create url
            URL url = new URL(placeURL);
            //open connection
            httpURLConnection = (HttpURLConnection) url.openConnection();
            //connect
            httpURLConnection.connect();

            inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuffer stringBuffer = new StringBuffer();

            String line = "";

            while ((line = bufferedReader.readLine()) != null)
            {
                stringBuffer.append(line);
            }

            //converting string buffer to string
            Data = stringBuffer.toString();
            bufferedReader.close();
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        //even after exception we want to execute we write it in the finally
        finally
        {
                inputStream.close();
                httpURLConnection.disconnect();

        }
        return Data;

    }
}
