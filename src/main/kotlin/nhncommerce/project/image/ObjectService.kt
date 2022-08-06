package nhncommerce.project.image

import org.apache.tomcat.util.http.fileupload.IOUtils
import org.springframework.http.HttpMethod
import org.springframework.http.client.SimpleClientHttpRequestFactory
import org.springframework.web.client.HttpMessageConverterExtractor
import org.springframework.web.client.RequestCallback
import org.springframework.web.client.RestTemplate
import java.io.File
import java.io.FileInputStream
import java.io.InputStream

class ObjectService(storageUrl: String?, tokenId: String?) {
    var tokenId: String? = null
    var storageUrl: String? = null
    var restTemplate: RestTemplate

    init {
        this.storageUrl = storageUrl
        this.tokenId = tokenId
        restTemplate = RestTemplate()
    }

    private fun getUrl(containerName: String, objectName: String): String {
        return this.storageUrl + "/" + containerName + "/" + objectName
    }

    fun uploadObject(containerName: String, objectName: String, inputStream: InputStream?) {
        println("upload object 진입")
        var url = getUrl(containerName, objectName)
        println("url : $url")
        // InputStream을 요청 본문에 추가할 수 있도록 RequestCallback 오버라이드
        var requestCallback = RequestCallback { request ->
            request.headers.add("X-Auth-Token", tokenId)
            IOUtils.copy(inputStream, request.body)
        }
        print("request call back : $requestCallback")

        // 오버라이드한 RequestCallback을 사용할 수 있도록 설정
        var requestFactory = SimpleClientHttpRequestFactory()
        requestFactory.setBufferRequestBody(false)
        var restTemplate = RestTemplate(requestFactory)
        var responseExtractor = HttpMessageConverterExtractor(
            String::class.java, restTemplate.messageConverters
        )

        // API 호출
        restTemplate.execute(url, HttpMethod.PUT, requestCallback, responseExtractor)
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            var storageUrl = "https://api-storage.cloud.toast.com/v1/AUTH_507cc2a432bc43de8721f24810f3daa1"
            var tokenId = "gAAAAABi6frBSTfcv8n4EM63io8NArjCiIif2ME3EWiwM6sEfpSeHEP-I1OcYjxk3s5VSneKk1M1puEuwfzj8RLr1r5h1CSFQOJa-iBPeblsZwdN9COFTZkEjO1asWuzVLVi3wcA_iPJD5X3YG7jkTyB_WG3jxuYwbkampCqpW8zs2mlmCr4Xpk"
            var containerName = "test"
            var objectPath = "/Users/soonbum/Documents/"
            var objectName = "shot.png"
            var objectService = ObjectService(storageUrl, tokenId)
            try {
                // 파일로 부터 InputStream 생성
                var objFile = File("$objectPath/$objectName")
                var inputStream: InputStream = FileInputStream(objFile)

                // 업로드
                objectService.uploadObject(containerName, objectName, inputStream)
                println("\nUpload OK")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}