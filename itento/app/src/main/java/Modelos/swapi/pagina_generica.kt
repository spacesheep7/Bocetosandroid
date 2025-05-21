package Modelos.swapi

import com.google.gson.annotations.SerializedName

data class PokemonResponse(
    @SerializedName("sprites") val sprites: Sprites
)

data class Sprites(
    @SerializedName("front_default") val frontDefault: String?
)