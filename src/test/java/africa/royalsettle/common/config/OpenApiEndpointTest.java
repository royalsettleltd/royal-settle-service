package africa.royalsettle.common.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class OpenApiEndpointTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void exposesOpenApiDocumentation() throws Exception {
        String document = mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.info.title").value("Royal Settle API"))
                .andExpect(jsonPath("$.components.securitySchemes.bearerAuth").exists())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode openApi = objectMapper.readTree(document);
        assertErrorResponseCode(openApi, "/auth/refresh-token", "post", "400", "400000");
        assertErrorResponseCode(openApi, "/thrift/create", "post", "403", "403000");
        assertErrorResponseCode(openApi, "/auth/refresh-token", "post", "500", "500000");
    }

    private void assertErrorResponseCode(
            JsonNode openApi, String path, String method, String httpStatus, String responseCode) {
        JsonNode response = openApi.path("paths")
                .path(path)
                .path(method)
                .path("responses")
                .path(httpStatus);

        assertThat(response.isMissingNode()).isFalse();
        assertThat(response.toString())
                .contains("\"responseCode\":\"" + responseCode + "\"")
                .doesNotContain("\"responseCode\":\"20000\"");
    }
}
