package com.cnbot.baiduvoice.speech.utils;

import com.aispeech.DUILiteConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wuwei on 18-6-6.
 */

public class SampleConstants {

    //aec res
    public static final String AEC_RES = "AEC_ch4-2-ch2_2ref_emd_20200508_v2.50.0.10.bin";
    //wakeup res
    public static final String WAKEUP_RES = "wakeup_aifar_comm_20180104.bin";
    //echo module res
    public static final String ECHO__RES = "AEC_ch2-2-ch1_1ref_emd_20200508_v2.50.0.10.bin";

    public static final String ECHO__RES_ASR = "AEC_ch8-2-ch6_2ref_emd_20200508_v2.50.0.10.bin";
    public static final String ECHO__RES_VOIP = "AEC_ch8-2-ch6_2ref_emd_20201105_v2.50.0.27_voip.bin";
    public static final String ECHO__RES_VOIP_ASR = "AEC_ch8-2-ch6_2ref_emd_20201105_v2.50.0.27_voip_asr.bin";
    /**
     * fespcar 模块
     */
    public static final String FESPCAR_RES = "UDA_asr_ch2_2_ch2_30mm_20190108_v1.1.0.8_car.bin";
    //beamforming res
    public static final String DUAL_BEAMFORMING_RES = "sspe_uda_wkp_30mm_ch2_v2.0.0.65.bin";
    public static final String LINE4_BEAMFORMING_RES = "sspe_ula_wkp_35mm_ch4_v2.0.0.65.bin";
    public static final String CIRCLE4_BEAMFORMING_RES = "sspe_uca_wkp_70mm_ch4_v2.0.0.65.bin";
    public static final String CIRCLE6_BEAMFORMING_RES = "sspe_uca_wkp_72mm_ch6_v2.0.0.65.bin";

    /**
     * Sevc 使用，sspe_aec_bss_nr_agc_ch6_3mic_2ref_v2.0.0.60.bin 3mic+2ref 5路音频，有1路去掉了
     */
    public static final String SEVC_SSPE_RES = "sspe_aec_bss_nr_agc_ch6_3mic_2ref_v2.0.0.60.bin";

    // sspe
    public static final String SSPE_RES = "sspe_aec_uda_bss_wkp_30mm_ch4_2mic_2ref_v2.0.0.65.bin";

    //vad module res
    public static final String VAD_RES = "vad_aihome_v0.11.bin";
    //local asr module res
    public static final String EBNFC_RES = "ebnfc.aicar.1.2.0_cn_en_merg.bin";
    public static final String EBNFR_RES = "ebnfr.aicar.1.3.0.bin";
    //local tts module res
    public static final String TTS_DICT_RES = "v2.1.21_aitts_sent_dict_local.db";
    public static final String TTS_DICT_MD5 = "v2.1.21_aitts_sent_dict_local.db.md5sum";
    public static final String TTS_FRONT_RES = "v2.1.21_local_front.bin";
    public static final String TTS_FRONT_RES_MD5 = "v2.1.21_local_front.bin.md5sum";
    // cjhaof 的神经网络模型, 在发音人的cfg文件中新增 "lstm_bin":"res/bin/cjhaof_lstm_baseline.bin","method":3  两个配置项
    // public static final String TTS_ = "cjhaof_lstm_baseline.bin";

    public static final String TTS_BACK_RES_LUCY = "lucyf_common_back_ce_local.v2.1.0.bin";
    public static final String TTS_BACK_RES_LUCY_MD5 = "lucyf_common_back_ce_local.v2.1.0.bin.md5sum";
    public static final String TTS_BACK_RES_ZHILING = "zhilingf_common_back_ce_local.v2.1.0.bin";
    public static final String TTS_BACK_RES_ZHILING_MD5 = "zhilingf_common_back_ce_local.v2.1.0.bin.md5sum";
    public static final String TTS_BACK_RES_XIJUN = "xijunm_common_back_ce_local.v2.1.0.bin";
    public static final String TTS_BACK_RES_XIJUN_MD5 = "xijunm_common_back_ce_local.v2.1.0.bin.md5sum";
    //vprint module res
    public static final String ASRPP_RES = "gender_local_model_dir_2019-09-02.bin";

    public static final String VP_RES_1mic = "vprint_nhxc_1mic_20190930_float.bin";
    public static final String VP_RES_2mic = "vprint_nhxc_2mic_20190930_float.bin";
    public static final String WAKEUP_VP_RES = "wakeup_aicar_comm_v0.19.0_vp1.3.bin";

    // vprint beamforming res
    public static final String VPRINT_DUAL_BEAMFORMING_RES = DUAL_BEAMFORMING_RES;

    //semantic module res
    public static final String ASR_RES = "ngram.foryouge.v0.7.bin";
    public static final String ASR_NET = "local_asr.net.bin";

    public static final String NR_RES = "NR_ch1-2-ch1_com_20171117_v1.0.0.bin";

    public static final String FASP_RES = "FEND_WAV2_MIC2_V2.90.0.2_BSS.bin";

    // mds
    public static final String MDS_RES_SINGLE = "mds_offline_ch1_1_0_config_20200714_v2.70.0.9.bin";
    public static final String MDS_RES_DUAL = "mds_offline_ch2_2_0_config_20200714_v2.70.0.9.bin";

    public static final String MIC_SINGLE = "单麦";
    public static final String MIC_SINGLE_ECHO = "单麦Echo";
    public static final String MIC_DUAL = "双麦";
    public static final String MIC_LINE4 = "线性4麦";
    public static final String MIC_CIRCLE4 = "环形4麦";
    public static final String MIC_CIRCLE6 = "环形6麦";
    public static final List<String> MIC_LIST;
    public static final Map<String, Integer> MIC_NAME_TYPE_MAP;
    public static final Map<Integer, String> MIC_TYPE_NAME_MAP;

    static {
        MIC_LIST = new ArrayList<>();
        MIC_LIST.add(MIC_SINGLE);
        MIC_LIST.add(MIC_SINGLE_ECHO);
        MIC_LIST.add(MIC_DUAL);
        MIC_LIST.add(MIC_LINE4);
        MIC_LIST.add(MIC_CIRCLE4);
        MIC_LIST.add(MIC_CIRCLE6);

        MIC_NAME_TYPE_MAP = new HashMap<>();
        MIC_NAME_TYPE_MAP.put(MIC_SINGLE, DUILiteConfig.TYPE_COMMON_MIC);
        MIC_NAME_TYPE_MAP.put(MIC_SINGLE_ECHO, DUILiteConfig.TYPE_COMMON_ECHO);
        MIC_NAME_TYPE_MAP.put(MIC_DUAL, DUILiteConfig.TYPE_COMMON_DUAL);
        MIC_NAME_TYPE_MAP.put(MIC_LINE4, DUILiteConfig.TYPE_COMMON_LINE4);
        MIC_NAME_TYPE_MAP.put(MIC_CIRCLE4, DUILiteConfig.TYPE_COMMON_CIRCLE4);
        MIC_NAME_TYPE_MAP.put(MIC_CIRCLE6, DUILiteConfig.TYPE_COMMON_CIRCLE6);

        MIC_TYPE_NAME_MAP = new HashMap<>();
        MIC_TYPE_NAME_MAP.put(DUILiteConfig.TYPE_COMMON_MIC, MIC_SINGLE);
        MIC_TYPE_NAME_MAP.put(DUILiteConfig.TYPE_COMMON_ECHO, MIC_SINGLE_ECHO);
        MIC_TYPE_NAME_MAP.put(DUILiteConfig.TYPE_COMMON_DUAL, MIC_DUAL);
        MIC_TYPE_NAME_MAP.put(DUILiteConfig.TYPE_COMMON_LINE4, MIC_LINE4);
        MIC_TYPE_NAME_MAP.put(DUILiteConfig.TYPE_COMMON_CIRCLE4, MIC_CIRCLE4);
        MIC_TYPE_NAME_MAP.put(DUILiteConfig.TYPE_COMMON_CIRCLE6, MIC_CIRCLE6);
    }
}
