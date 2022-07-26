package com.example.success.constant;

import java.util.Random;

public class Constants {

    public enum JobState {

        INVALID("invalid", "0"),
        ABORTED("aborted", "0"),
        ENCODING("encoding",  randomProgress(10, 99)), // using random in range (10, 99)
        COMPLETE("complete", "100");

        private String state;

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getProgress() {
            return progress;
        }

        public void setProgress(String progress) {
            this.progress = progress;
        }

        private String progress;

        JobState(String state, String progress) {
            this.state = state;
            this.progress = progress;
        }
    }



    private static String randomProgress(int min, int max) {
        Random rn = new Random();
        int ret = min + rn.nextInt(max - min + 1);
        String retStr = String.valueOf(ret);
        return retStr;
    }

}
