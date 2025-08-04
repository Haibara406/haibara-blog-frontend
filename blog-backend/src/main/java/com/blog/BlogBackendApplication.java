package com.blog;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@SpringBootApplication
@MapperScan("com.blog.mapper")
@EnableMethodSecurity
@Slf4j
public class BlogBackendApplication {

    public static void main(String[] args) {

        SpringApplication.run(BlogBackendApplication.class, args);

        log.info(
                """
                \n
                ------------------------------------------------------------------------------------------------------------恭喜你成功启动后端---------------------------------------------------------------------------------------------------------
                        
                        
                                  WWWWWWWW                           WWWWWWWW               lllllll                                                                                       TTTTTTTTTTTTTTTTTTTTTTT                 \s
                                  W::::::W                           W::::::W               l:::::l                                                                                       T:::::::::::::::::::::T                 \s
                                  W::::::W                           W::::::W               l:::::l                                                                                       T:::::::::::::::::::::T                 \s
                                  W::::::W                           W::::::W               l:::::l                                                                                       T:::::TT:::::::TT:::::T                 \s
                                   W:::::W           WWWWW           W:::::W eeeeeeeeeeee    l::::l     cccccccccccccccc   ooooooooooo      mmmmmmm    mmmmmmm       eeeeeeeeeeee         TTTTTT  T:::::T  TTTTTTooooooooooo      \s
                                    W:::::W         W:::::W         W:::::Wee::::::::::::ee  l::::l   cc:::::::::::::::c oo:::::::::::oo  mm:::::::m  m:::::::mm   ee::::::::::::ee               T:::::T      oo:::::::::::oo    \s
                                     W:::::W       W:::::::W       W:::::We::::::eeeee:::::eel::::l  c:::::::::::::::::co:::::::::::::::om::::::::::mm::::::::::m e::::::eeeee:::::ee             T:::::T     o:::::::::::::::o   \s
                                      W:::::W     W:::::::::W     W:::::We::::::e     e:::::el::::l c:::::::cccccc:::::co:::::ooooo:::::om::::::::::::::::::::::me::::::e     e:::::e             T:::::T     o:::::ooooo:::::o   \s
                                       W:::::W   W:::::W:::::W   W:::::W e:::::::eeeee::::::el::::l c::::::c     ccccccco::::o     o::::om:::::mmm::::::mmm:::::me:::::::eeeee::::::e             T:::::T     o::::o     o::::o   \s
                                        W:::::W W:::::W W:::::W W:::::W  e:::::::::::::::::e l::::l c:::::c             o::::o     o::::om::::m   m::::m   m::::me:::::::::::::::::e              T:::::T     o::::o     o::::o   \s
                                         W:::::W:::::W   W:::::W:::::W   e::::::eeeeeeeeeee  l::::l c:::::c             o::::o     o::::om::::m   m::::m   m::::me::::::eeeeeeeeeee               T:::::T     o::::o     o::::o   \s
                                          W:::::::::W     W:::::::::W    e:::::::e           l::::l c::::::c     ccccccco::::o     o::::om::::m   m::::m   m::::me:::::::e                        T:::::T     o::::o     o::::o   \s
                                           W:::::::W       W:::::::W     e::::::::e         l::::::lc:::::::cccccc:::::co:::::ooooo:::::om::::m   m::::m   m::::me::::::::e                     TT:::::::TT   o:::::ooooo:::::o   \s
                                            W:::::W         W:::::W       e::::::::eeeeeeee l::::::l c:::::::::::::::::co:::::::::::::::om::::m   m::::m   m::::m e::::::::eeeeeeee             T:::::::::T   o:::::::::::::::o   \s
                                             W:::W           W:::W         ee:::::::::::::e l::::::l  cc:::::::::::::::c oo:::::::::::oo m::::m   m::::m   m::::m  ee:::::::::::::e             T:::::::::T    oo:::::::::::oo    \s
                                              WWW             WWW            eeeeeeeeeeeeee llllllll    cccccccccccccccc   ooooooooooo   mmmmmm   mmmmmm   mmmmmm    eeeeeeeeeeeeee             TTTTTTTTTTT      ooooooooooo      \s
                                                                        bbbbbbbb                                                                                                                                                  \s
                        HHHHHHHHH     HHHHHHHHH                    iiii b::::::b                                                                                  BBBBBBBBBBBBBBBBB   lllllll                                     \s
                        H:::::::H     H:::::::H                   i::::ib::::::b                                                                                  B::::::::::::::::B  l:::::l                                     \s
                        H:::::::H     H:::::::H                    iiii b::::::b                                                                                  B::::::BBBBBB:::::B l:::::l                                     \s
                        HH::::::H     H::::::HH                          b:::::b                                                                                  BB:::::B     B:::::Bl:::::l                                     \s
                          H:::::H     H:::::H    aaaaaaaaaaaaa   iiiiiii b:::::bbbbbbbbb      aaaaaaaaaaaaa  rrrrr   rrrrrrrrr   aaaaaaaaaaaaa                      B::::B     B:::::B l::::l    ooooooooooo      ggggggggg   ggggg
                          H:::::H     H:::::H    a::::::::::::a  i:::::i b::::::::::::::bb    a::::::::::::a r::::rrr:::::::::r  a::::::::::::a                     B::::B     B:::::B l::::l  oo:::::::::::oo   g:::::::::ggg::::g
                          H::::::HHHHH::::::H    aaaaaaaaa:::::a  i::::i b::::::::::::::::b   aaaaaaaaa:::::ar:::::::::::::::::r aaaaaaaaa:::::a                    B::::BBBBBB:::::B  l::::l o:::::::::::::::o g:::::::::::::::::g
                          H:::::::::::::::::H             a::::a  i::::i b:::::bbbbb:::::::b           a::::arr::::::rrrrr::::::r         a::::a  ---------------   B:::::::::::::BB   l::::l o:::::ooooo:::::og::::::ggggg::::::gg
                          H:::::::::::::::::H      aaaaaaa:::::a  i::::i b:::::b    b::::::b    aaaaaaa:::::a r:::::r     r:::::r  aaaaaaa:::::a  -:::::::::::::-   B::::BBBBBB:::::B  l::::l o::::o     o::::og:::::g     g:::::g\s
                          H::::::HHHHH::::::H    aa::::::::::::a  i::::i b:::::b     b:::::b  aa::::::::::::a r:::::r     rrrrrrraa::::::::::::a  ---------------   B::::B     B:::::B l::::l o::::o     o::::og:::::g     g:::::g\s
                          H:::::H     H:::::H   a::::aaaa::::::a  i::::i b:::::b     b:::::b a::::aaaa::::::a r:::::r           a::::aaaa::::::a                    B::::B     B:::::B l::::l o::::o     o::::og:::::g     g:::::g\s
                          H:::::H     H:::::H  a::::a    a:::::a  i::::i b:::::b     b:::::ba::::a    a:::::a r:::::r          a::::a    a:::::a                    B::::B     B:::::B l::::l o::::o     o::::og::::::g    g:::::g\s
                        HH::::::H     H::::::HHa::::a    a:::::a i::::::ib:::::bbbbbb::::::ba::::a    a:::::a r:::::r          a::::a    a:::::a                  BB:::::BBBBBB::::::Bl::::::lo:::::ooooo:::::og:::::::ggggg:::::g\s
                        H:::::::H     H:::::::Ha:::::aaaa::::::a i::::::ib::::::::::::::::b a:::::aaaa::::::a r:::::r          a:::::aaaa::::::a                  B:::::::::::::::::B l::::::lo:::::::::::::::o g::::::::::::::::g\s
                        H:::::::H     H:::::::H a::::::::::aa:::ai::::::ib:::::::::::::::b   a::::::::::aa:::ar:::::r           a::::::::::aa:::a                 B::::::::::::::::B  l::::::l oo:::::::::::oo   gg::::::::::::::g\s
                        HHHHHHHHH     HHHHHHHHH  aaaaaaaaaa  aaaaiiiiiiiibbbbbbbbbbbbbbbb     aaaaaaaaaa  aaaarrrrrrr            aaaaaaaaaa  aaaa                 BBBBBBBBBBBBBBBBB   llllllll   ooooooooooo       gggggggg::::::g\s
                                                                                                                                                                                                                           g:::::g\s
                                                                                                                                                                                                               gggggg      g:::::g\s
                                                                                                                                                                                                               g:::::gg   gg:::::g\s
                                                                                                                                                                                                                g::::::ggg:::::::g\s
                                                                                                                                                                                                                 gg:::::::::::::g \s
                                                                                                                                                                                                                   ggg::::::ggg   \s
                                                                                                                                                                                                                      gggggg    \s
                """
        );
    }
}
