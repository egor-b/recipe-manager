package com.foodcrunch.foodster.recipemanager.controller;

import com.foodcrunch.foodster.recipemanager.exception.BadRequestException;
import com.foodcrunch.foodster.recipemanager.service.RecipeFoodManagerService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

@RunWith(SpringJUnit4ClassRunner.class)
@WebFluxTest(FoodManagerController.class)
public class FoodManagerControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private RecipeFoodManagerService recipeFoodManagerService;

    @Test
    @WithMockUser
    public void whenGetValidString_thenReturnFoodList() {
        Mockito.when(recipeFoodManagerService.getFoodByName("b", 0, 15, Sort.Direction.ASC, "name"))
                .thenReturn(TestValue.getFoodList(3));
        webTestClient.mutateWith(csrf())
                .get()
                .uri("/v1/food/search?name=b")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk();
        verify(recipeFoodManagerService, times(1)).getFoodByName("b", 0, 15, Sort.Direction.ASC, "name");
    }

    @Test
    @WithMockUser
    public void whenGetValidStringAndInvalidPageSize_thenReturnBadRequestException() {
        when(recipeFoodManagerService.getFoodByName("B", 0, 999, Sort.Direction.ASC, "name")).thenReturn(Flux.error(new BadRequestException("Error")));
        webTestClient.mutateWith(csrf())
                .get()
                .uri("/v1/food/search?name=B&page_size=999")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isBadRequest();
        verify(recipeFoodManagerService, times(1)).getFoodByName("B", 0, 999, Sort.Direction.ASC, "name");
    }

    @Test
    @WithMockUser
    public void whenGetInvalidString_thenReturnBadRequestException() {
        webTestClient.mutateWith(csrf())
                .get()
                .uri("/v1/food/search")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isBadRequest();
        verify(recipeFoodManagerService, times(0)).getFoodByName("B", 0, 999, Sort.Direction.ASC, "name");
    }

}
