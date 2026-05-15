import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class ProductJson(
    @SerialName("Номенклатура") val details: ProductDetails
)

@Serializable
data class ProductDetails(
    @SerialName("Наименование") val name: String,
    @SerialName("ГУИД1С") val guid: String,
    @SerialName("Штрихкод") val barcode: String,
    @SerialName("Характеристики") val specifications: List<SpecificationJson> = emptyList()
)

@Serializable
data class SpecificationJson(
    @SerialName("Наименование") val name: String,
    @SerialName("ГУИД1С") val guid: String,
    @SerialName("Штрихкод") val barcode: String
)