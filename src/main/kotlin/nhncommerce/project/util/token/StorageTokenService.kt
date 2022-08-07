package nhncommerce.project.util.token

import nhncommerce.project.image.AuthService
import nhncommerce.project.image.ImageService
import nhncommerce.project.util.token.domain.StorageToken
import org.json.JSONObject

import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Service
class StorageTokenService(
    val storageTokenRepository: StorageTokenRepository,
    val authService: AuthService,
    val imageService: ImageService
) {

    fun saveToken(tokenId : String, expires : LocalDateTime){
        val storageToken = StorageToken(tokenId = tokenId, expires = expires)
        storageTokenRepository.save(storageToken)
        imageService.insertTokenId(getTokenId())
    }

    fun updateToken(tokenId : String, expires : LocalDateTime){
        val getToken = storageTokenRepository.findAll().get(0)
        getToken.apply {
            this.tokenId = tokenId
            this.expires = expires
        }
        storageTokenRepository.save(getToken)
        imageService.insertTokenId(getTokenId())
    }

    fun hasToken(): Boolean {
        return storageTokenRepository.findAll().isNotEmpty()
    }

    fun checkExpired(){
        val getToken = storageTokenRepository.findAll().get(0)
        if(getToken.expires.isBefore(LocalDateTime.now())){ //만료
            //발급
            val generateToken = authService.generateToken()
            val expires = expiresParser(generateToken!!)
            val tokenId = tokenIdParser(generateToken!!)
            updateToken(tokenId,expires)
            println("updateToken-------------------------------")
        }
    }

    fun expiresParser(jsonArray : String) : LocalDateTime{
        val jsonObject = JSONObject(jsonArray)
        val jsonAccess = jsonObject.getJSONObject("access")
        val jsonToken = jsonAccess.getJSONObject("token")
        val tokenExpires = jsonToken.getString("expires")

        val formatter = DateTimeFormatter.ofPattern( "yyyy-MM-dd'T'HH:mm:ss'Z'")
        val expires : LocalDateTime= LocalDateTime.parse(tokenExpires,formatter)

        return expires
    }

    fun tokenIdParser(jsonArray : String) : String{
        val jsonObject = JSONObject(jsonArray)
        val jsonAccess = jsonObject.getJSONObject("access")
        val jsonToken = jsonAccess.getJSONObject("token")
        val tokenId = jsonToken.getString("id")

        return tokenId
    }

    fun generateToken(){
        val generateToken = authService.generateToken()
        val expires = expiresParser(generateToken!!)
        val tokenId = tokenIdParser(generateToken!!)
        saveToken(tokenId, expires)
        imageService.insertTokenId(getTokenId())
    }

    fun getTokenId() : String{
        return storageTokenRepository.findAll().get(0).tokenId
    }

}