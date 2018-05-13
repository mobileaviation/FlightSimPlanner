package nl.robenanita.googlemapstest.Firebase;

public interface FBTableDownloadProgress {
    public void onProgress(Integer count, Integer downloaded, FBTableType tableType);
}
