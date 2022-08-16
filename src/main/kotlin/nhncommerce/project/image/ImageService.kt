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
class ImageService {
    //todo : 불변값 val, 떠는 yml로 관리하자
    //todo : private으로 관리
    var storageUrl = "https://api-storage.cloud.toast.com/v1/AUTH_507cc2a432bc43de8721f24810f3daa1"
    var containerName = "kirin"
    var tokenId = ""
    var restTemplate = RestTemplate()

    fun insertTokenId(tokenId : String){
        this.tokenId = tokenId
    }

    private fun getUrl(containerName: String, objectName: String): String {
        //todo : 문자열 탬플릿으로 변경
        return this.storageUrl + "/" + containerName + "/" + objectName
    }

    fun uploadObject(containerName: String, objectName: String, inputStream: InputStream?) : String{
        val url = getUrl(containerName, objectName)
        val requestCallback = RequestCallback { request ->
            request.headers.add("X-Auth-Token", tokenId) //todo : static 하게 사용하는게 좋다.
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

    fun deleteObject(objectName: String?) { //todo : ? 를 없애면 관리하기가 좋다.
        val url = getUrl(containerName!!, objectName!!) //todo : !!를 안쓰는게 좋다. 관리가 안된다. npe가 발생할수있다. //
        val headers = HttpHeaders()
        headers.add("X-Auth-Token", tokenId)
        val requestHttpEntity: HttpEntity<String> = HttpEntity<String>(null, headers)
        restTemplate.exchange(url, HttpMethod.DELETE, requestHttpEntity, String::class.java)
    }

    fun uploadImage(inputStream: InputStream?) : String{
        //todo : catch에서 관리를 해줘야한다. 로그를 찍는등
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