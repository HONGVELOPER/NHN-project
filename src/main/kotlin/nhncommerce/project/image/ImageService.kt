package nhncommerce.project.image

import org.apache.tomcat.util.http.fileupload.IOUtils
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.client.SimpleClientHttpRequestFactory
import org.springframework.stereotype.Service
import org.springframework.web.client.HttpMessageConverterExtractor
import org.springframework.web.client.RequestCallback
import org.springframework.web.client.RestTemplate
import java.io.InputStream
import java.util.*

@Service
class ImageService {

    @Value("\${image.storageUrl}")
    private var storageUrl = ""

    @Value("\${image.containerName}")
    var containerName = ""

    var tokenId = ""
    var restTemplate = RestTemplate()

    fun insertTokenId(tokenId : String){
        this.tokenId = tokenId
    }

    private fun getUrl(containerName: String, objectName: String): String {
        return "${this.storageUrl}/$containerName/$objectName"
    }

    fun uploadObject(containerName: String, objectName: String, inputStream: InputStream?) : String{
        val url = getUrl(containerName, objectName)
        val requestCallback = RequestCallback { request ->
            request.headers.add("X-Auth-Token", tokenId)
            IOUtils.copy(inputStream, request.body)
        }
        val requestFactory = SimpleClientHttpRequestFactory()
        requestFactory.setBufferRequestBody(false)
        val restTemplate = RestTemplate(requestFactory)

        val responseExtractor = HttpMessageConverterExtractor(
            String::class.java, restTemplate.messageConverters
        )
        restTemplate.execute(url, HttpMethod.PUT, requestCallback, responseExtractor)
        return url
    }

    fun deleteObject(objectName: String) {
        val url = getUrl(containerName, objectName)
        val headers = HttpHeaders()
        headers.add("X-Auth-Token", tokenId)
        val requestHttpEntity: HttpEntity<String> = HttpEntity<String>(null, headers)
        restTemplate.exchange(url, HttpMethod.DELETE, requestHttpEntity, String::class.java)
    }

    fun uploadImage(inputStream: InputStream?) : String{
        try {
            val uuid = UUID.randomUUID().toString()
            return uploadObject(containerName, uuid, inputStream)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }

    fun deleteImage(objectName : String){
        try {
            deleteObject(objectName)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

}