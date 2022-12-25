package com.example.mojezakupy.helpers;

import android.util.Log;

import com.google.firebase.ml.vision.text.FirebaseVisionText;

public class TextProcessHelper {
    public String[] process(FirebaseVisionText firebaseVisionText) {
        StringBuilder resultText = new StringBuilder();

        for (FirebaseVisionText.TextBlock block: firebaseVisionText.getTextBlocks()) {
            if(block.getText().length() > 5) {
                resultText.append(block.getText());
            }
        }

        String cleanDigitalInString = resultText.toString().replaceAll("\\D+","");
        String lastTwoDigits = GetTwoLastNumbers(cleanDigitalInString);

        if(!cleanDigitalInString.equals(lastTwoDigits)){
            cleanDigitalInString = cleanDigitalInString.substring(0, cleanDigitalInString.length() - 2) + "." + lastTwoDigits;
        }
        String[] arrayToReturn = new String[2];
        arrayToReturn[0] = cleanDigitalInString;
        arrayToReturn[1] =  resultText.toString();

        return arrayToReturn;
    }

    private String GetTwoLastNumbers(String string) {
        if(string != null || !(string.length() <= 2)) {
            return string.substring(string.length() - 2);
        }

        return string;
    }
}
