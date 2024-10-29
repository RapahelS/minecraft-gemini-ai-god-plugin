package net.bigyous.gptgodmc.GPT.Json;

import com.google.gson.annotations.SerializedName;

public enum AudioModel {
    @SerializedName("simba-english")
    simba_english,
    @SerializedName("simba-base")
    simba_base,
    @SerializedName("simba-multilingual")
    simba_multilingual,
    @SerializedName("simba-turbo")
    simba_turbo,
}