package com.acme.security;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;
import org.thymeleaf.spring5.view.ThymeleafView;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static java.util.Arrays.asList;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.http.MediaType.APPLICATION_XML_VALUE;
import static org.springframework.http.MediaType.TEXT_HTML_VALUE;

@SpringBootApplication
public class SecurityApplication {
  public static void main(String[] args) {
    SpringApplication.run(SecurityApplication.class, args);
  }

  @Bean(name = {"jsonView"})
  MappingJackson2JsonView jsonView(Jackson2ObjectMapperBuilder builder) {
    return new MappingJackson2JsonView(builder.build());
  }
}

@Configuration
class WebConfiguration implements WebMvcConfigurer {
  private final MappingJackson2JsonView jsonView;
  private final ThymeleafView thymeleafView;

  public WebConfiguration(MappingJackson2JsonView jsonView, ThymeleafView thymeleafView) {
    this.jsonView = jsonView;
    this.thymeleafView = thymeleafView;
  }

  @Override
  public void configureViewResolvers(ViewResolverRegistry registry) {
    registry.enableContentNegotiation(jsonView, thymeleafView);
  }
}

//class BeanAliasBeanRegistrar implements ImportBeanDefinitionRegistrar, PriorityOrdered {
//  @Override
//  public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
//    MultiValueMap<String, Object> annotationAttributes =
//        metadata.getAllAnnotationAttributes(BeanAlias.class.getName());
//    assert annotationAttributes != null;
//    String existingBeanName = (String) annotationAttributes.getFirst("existingBeanName");
//    annotationAttributes.get("aliases").forEach(alias -> {
//      assert existingBeanName != null;
//      registry.registerAlias(existingBeanName, alias.toString());
//    });
//  }
//
//  @Override
//  public int getOrder() {
//    return 0;
//  }
//}
//
//
//@Target(ElementType.TYPE)
//@Retention(RetentionPolicy.RUNTIME)
//@Import(BeanAliasBeanRegistrar.class)
//@interface BeanAlias {
//  String existingBeanName();
//  String[] aliases();
//}

@Controller
//@BeanAlias(existingBeanName = "mappingJackson2JsonView", aliases = {"/products"})
class ProductResource {
  @GetMapping(produces = {APPLICATION_JSON_UTF8_VALUE, APPLICATION_XML_VALUE, TEXT_HTML_VALUE})
  String getAllProducts(Model model) {
    List<Product> products = asList(
        Product.builder().id(1).name("Product #1").quantity(ThreadLocalRandom.current().nextInt(1, 10)).build(),
        Product.builder().id(2).name("Product #2").quantity(ThreadLocalRandom.current().nextInt(1, 10)).build(),
        Product.builder().id(3).name("Product #3").quantity(ThreadLocalRandom.current().nextInt(1, 10)).build()
    );
    model.addAttribute("products", products);
    return "products";
  }
}
