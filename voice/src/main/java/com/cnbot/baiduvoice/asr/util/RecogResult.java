package com.cnbot.baiduvoice.asr.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * The type Recog result.
 *
 * @author Created by dj on 2019/8/29. asr语音识别结果解析。
 */
public class RecogResult {
    private static final int ERROR_NONE = 0;

    private String origalJson;
    private String[] resultsRecognition;
    private String origalResult;
    private String sn; // 日志id， 请求有问题请提问带上sn
    private String desc;
    private String resultType;
    private int error = -1;
    private int subError = -1;

    /**
     * Parse json recog result.
     *
     * @param jsonStr the json str
     * @return the recog result
     */
    public static RecogResult parseJson(String jsonStr) {
        RecogResult result = new RecogResult();
        result.setOrigalJson(jsonStr);
        try {
            JSONObject json = new JSONObject(jsonStr);
            int error = json.optInt("error");
            int subError = json.optInt("sub_error");
            result.setError(error);
            result.setDesc(json.optString("desc"));
            result.setResultType(json.optString("result_type"));
            result.setSubError(subError);
            if (error == ERROR_NONE) {
                result.setOrigalResult(json.getString("origin_result"));
                JSONArray arr = json.optJSONArray("results_recognition");
                if (arr != null) {
                    int size = arr.length();
                    String[] recogs = new String[size];
                    for (int i = 0; i < size; i++) {
                        recogs[i] = arr.getString(i);
                    }
                    result.setResultsRecognition(recogs);
                }


            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * Has error boolean.
     *
     * @return the boolean
     */
    public boolean hasError() {
        return error != ERROR_NONE;
    }

    /**
     * Is final result boolean.
     *
     * @return the boolean
     */
    public boolean isFinalResult() {
        return "final_result".equals(resultType);
    }


    /**
     * Is partial result boolean.
     *
     * @return the boolean
     */
    public boolean isPartialResult() {
        return "partial_result".equals(resultType);
    }

    /**
     * Is nlu result boolean.
     *
     * @return the boolean
     */
    public boolean isNluResult() {
        return "nlu_result".equals(resultType);
    }

    /**
     * Gets origal json.
     *
     * @return the origal json
     */
    public String getOrigalJson() {
        return origalJson;
    }

    /**
     * Sets origal json.
     *
     * @param origalJson the origal json
     */
    public void setOrigalJson(String origalJson) {
        this.origalJson = origalJson;
    }

    /**
     * Get results recognition string [ ].
     *
     * @return the string [ ]
     */
    public String[] getResultsRecognition() {
        return resultsRecognition;
    }

    /**
     * Sets results recognition.
     *
     * @param resultsRecognition the results recognition
     */
    public void setResultsRecognition(String[] resultsRecognition) {
        this.resultsRecognition = resultsRecognition;
    }

    /**
     * Gets sn.
     *
     * @return the sn
     */
    public String getSn() {
        return sn;
    }

    /**
     * Sets sn.
     *
     * @param sn the sn
     */
    public void setSn(String sn) {
        this.sn = sn;
    }

    /**
     * Gets error.
     *
     * @return the error
     */
    public int getError() {
        return error;
    }

    /**
     * Sets error.
     *
     * @param error the error
     */
    public void setError(int error) {
        this.error = error;
    }

    /**
     * Gets desc.
     *
     * @return the desc
     */
    public String getDesc() {
        return desc;
    }

    /**
     * Sets desc.
     *
     * @param desc the desc
     */
    public void setDesc(String desc) {
        this.desc = desc;
    }

    /**
     * Gets origal result.
     *
     * @return the origal result
     */
    public String getOrigalResult() {
        return origalResult;
    }

    /**
     * Sets origal result.
     *
     * @param origalResult the origal result
     */
    public void setOrigalResult(String origalResult) {
        this.origalResult = origalResult;
    }

    /**
     * Gets result type.
     *
     * @return the result type
     */
    public String getResultType() {
        return resultType;
    }

    /**
     * Sets result type.
     *
     * @param resultType the result type
     */
    public void setResultType(String resultType) {
        this.resultType = resultType;
    }

    /**
     * Gets sub error.
     *
     * @return the sub error
     */
    public int getSubError() {
        return subError;
    }

    /**
     * Sets sub error.
     *
     * @param subError the sub error
     */
    public void setSubError(int subError) {
        this.subError = subError;
    }
}
