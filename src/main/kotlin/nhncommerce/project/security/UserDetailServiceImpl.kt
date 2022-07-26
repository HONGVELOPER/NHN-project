//package nhncommerce.project.security
//
//import org.springframework.security.core.userdetails.UserDetailsService
//
//@Service
//class UserDetailsServiceImpl(private val userRepository: UserRepository) : UserDetailsService {
//
//    override fun loadUserByUsername(username: String): UserDetails {
//        val user: User = memberRepository.findByUsername(username) ?: throw UsernameNotFoundException("존재하지 않는 username 입니다.")
//
//        return UserDetailsImpl(member)
//    }
//}