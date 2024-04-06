package cn.rollin.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import springfox.documentation.builders.*;
import springfox.documentation.oas.annotations.EnableOpenApi;
import springfox.documentation.schema.ScalarType;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.ArrayList;
import java.util.List;

import static cn.rollin.constant.Constant.USER_PATH;

/**
 * Swagger 配置
 *
 * @author rollin
 * @date 2024-03-30 15:31:08
 */
@Configuration
@EnableOpenApi
public class SwaggerConfiguration {

    /**
     * Swagger 开关
     */
    @Value("${security.switchs.swaggerEnable}")
    private Boolean swaggerEnable;


    /**
     * 用户微服务接口文档
     *
     * @return docket对象
     */
    @Bean
    public Docket userApiDoc() {
        return new Docket(DocumentationType.OAS_30)
                .groupName("用户服务接口文档")
                .pathMapping("/")

                //定义是否开启Swagger，false是关闭，可以通过变量去控制，线上关闭
                .enable(swaggerEnable)

                //配置文档的元信息
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("cn.rollin"))

                //正则匹配请求路径，并分配到当前项目组
                .paths(PathSelectors.ant(USER_PATH + "/**"))
                .build()

                // 新版SwaggerUI3.0
                .globalRequestParameters(globalReqeustParameters())
                .globalResponses(HttpMethod.GET, getGlabalResponseMessage())
                .globalResponses(HttpMethod.POST, getGlabalResponseMessage());
    }

    /**
     * 对管理端的接口文档
     *
     * @return docket对象
     */
    @Bean
    public Docket adminApiDoc() {

        return new Docket(DocumentationType.OAS_30)
                .groupName("管理端接口文档")
                .pathMapping("/")

                //定义是否开启Swagger，false是关闭，可以通过变量去控制，线上关闭
                .enable(true)

                //配置文档的元信息
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("cn.rollin"))
                //正则匹配请求路径，并分配到当前项目组
                .paths(PathSelectors.ant("/admin/**"))
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("有钱记账接口文档")
                .description("有钱记账接口文档")
                .contact(new Contact("rollin", "", "18946025623@163.com"))
                .version("v1.0")
                .build();
    }

    /**
     * 配置全局通用参数
     *
     * @return docket对象
     */
    private List<RequestParameter> globalReqeustParameters() {

        List<RequestParameter> parameters = new ArrayList<>();
        parameters.add(new RequestParameterBuilder()
                .name("token")
                .description("登录令牌")
                .in(ParameterType.HEADER)
                .query(q -> q.model(m -> m.scalarModel(ScalarType.STRING)))
                .required(false)
                .build());
        return parameters;

    }

    /**
     * 生成通用的响应信息
     */

    private List<Response> getGlabalResponseMessage() {

        List<Response> list = new ArrayList<>();
        list.add(new ResponseBuilder()
                .code("4xx")
                .description("请求错误，根据code和msg检查")
                .build());
        return list;
    }
}


