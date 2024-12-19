package com.fireshare.ipfstest;

import android.util.Log;
import com.google.gson.Gson;
import hprose.client.HproseClient;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class IPFSTest {

    public static String uploadToIPFS() throws IOException, URISyntaxException {
        String baseUrl = "http://192.168.5.4:8080";
        String appId = "heWgeGkeBX2gaENbIBS_Iy1mdTS";

        HproseService client = HproseClient.create(baseUrl).useService(HproseService.class);
        byte[] buffer = new byte[32];
        new Random().nextBytes(buffer);

        String json = "{ \"aid\": \"" + appId + "\", \"ver\": \"last\", \"offset\": 0 }";
        HashMap request = new Gson().fromJson(json, HashMap.class);

        try {
            String fsid = client.runMApp("upload_ipfs", request, List.of(buffer));
            return (fsid != null) ? client.mfTemp2Ipfs(fsid, null) : null;
        } catch (Exception e) {
            Log.e("uploadToIPFS()", "Error: " + request + " " + e);
            return null;
        }
    }

    public interface HproseService {
        <T> T runMApp(String entry, Map<?, ?> request, List<byte[]> args);
        String mfTemp2Ipfs(String fsid, String ref);
    }
}