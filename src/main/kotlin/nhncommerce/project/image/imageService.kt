package nhncommerce.project.image

import org.apache.tomcat.util.http.fileupload.IOUtils
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
class imageService {
    var storageUrl = "https://api-storage.cloud.toast.com/v1/AUTH_507cc2a432bc43de8721f24810f3daa1"
    var tokenId = "gAAAAABi6y9d60vhrvhwHXLN-FBMTcV2juT-yhBcmoOV68V_6Mi_Kta3soHcLdqoHRKCa6HsQgsUtAcqVdMicA5rgFYY8hdCrWcZzSwrHWCe8tDmE3VHYtIsK40Rwb418tZlE3haZDb3ZBEE7VPqlWofZTgsPKr580VA9kxcJD-JAP_0NXUOito"
    var containerName = "test"
    var objectPath = "/Users/soonbum/Documents/"
    var objectName = ""
    var restTemplate = RestTemplate()

    private fun getUrl(containerName: String, objectName: String): String {
        return this.storageUrl + "/" + containerName + "/" + objectName
    }

    fun uploadObject(containerName: String, objectName: String, inputStream: InputStream?) : String{
        var url = getUrl(containerName, objectName)
        var requestCallback = RequestCallback { request ->
            request.headers.add("X-Auth-Token", tokenId)
            IOUtils.copy(inputStream, request.body)
        }
        var requestFactory = SimpleClientHttpRequestFactory()
        requestFactory.setBufferRequestBody(false)
        var restTemplate = RestTemplate(requestFactory)
        var responseExtractor = HttpMessageConverterExtractor(
            String::class.java, restTemplate.messageConverters
        )
        restTemplate.execute(url, HttpMethod.PUT, requestCallback, responseExtractor)
        return url
    }

    fun deleteObject(objectName: String?) {
        val url = getUrl(containerName!!, objectName!!)
        val headers = HttpHeaders()
        headers.add("X-Auth-Token", tokenId)
        val requestHttpEntity: HttpEntity<String> = HttpEntity<String>(null, headers)
        restTemplate.exchange(url, HttpMethod.DELETE, requestHttpEntity, String::class.java)
    }

    fun uploadImage(inputStream: InputStream?) : String{
        try {
            var imageService = imageService()
            var uuid = UUID.randomUUID().toString()
            val url = imageService.uploadObject(containerName, uuid, inputStream)
            return url
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }

    fun deleteImage(objectName : String){
        var imageService = imageService()
        try {
            imageService.deleteObject(objectName)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

}