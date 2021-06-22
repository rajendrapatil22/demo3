package com.example.controller;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.util.Arrays;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.example.builders.ItemBuilder;
import com.example.model.Item;
import com.example.repository.ItemRepository;
import com.google.gson.Gson;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
@RunWith(MockitoJUnitRunner.class)
public class ItemControllerTest {
  private static final int CHECKED_ITEM_ID = 1;
  private static final Item CHECKED_ITEM = new ItemBuilder()
    .id(CHECKED_ITEM_ID)
    .checked()
    .build();
  private static final Item UNCHECKED_ITEM = new ItemBuilder()
    .id(2)
    .checked()
    .build();
  private static final Item NEW_ITEM = new ItemBuilder()
    .checked()
    .build();
  @InjectMocks
  private ItemController controller;
  @Mock
  private ItemRepository repository;
  private ArgumentCaptor<Item> anyItem = ArgumentCaptor.forClass(Item.class);
  
  @Test
  public void whenFindingItemsItShouldReturnAllItems() {

    given(repository.findAll()).willReturn(Arrays.asList(CHECKED_ITEM, UNCHECKED_ITEM));

    assertThat(controller.findItems())
 
    .containsOnly(CHECKED_ITEM, UNCHECKED_ITEM);
  }

  @Test
  public void whenAddingItemItShouldReturnTheSavedItem() {

    given(repository.saveAndFlush(NEW_ITEM)).willReturn(CHECKED_ITEM);

    assertThat(controller.addItem(NEW_ITEM))
   
    .isSameAs(CHECKED_ITEM);
  }
  
  @Test
  public void whenAddingItemItShouldMakeSureNoIDIsPassed() {
  
    controller.addItem(CHECKED_ITEM);
   
    verify(repository).saveAndFlush(anyItem.capture());
  
    assertThat(anyItem.getValue().getId()).isNull();
  }
  
  @Test
  public void whenUpdatingItemItShouldReturnTheSavedItem() {

    given(repository.getOne(CHECKED_ITEM_ID)).willReturn(CHECKED_ITEM);

    given(repository.saveAndFlush(CHECKED_ITEM)).willReturn(CHECKED_ITEM);
   
    assertThat(controller.updateItem(CHECKED_ITEM, CHECKED_ITEM_ID))

    .isSameAs(CHECKED_ITEM);
  }
  @Test
  public void testItemList() {
	Response resp=(Response) RestAssured.given().when().get("http://localhost:8086/items/").then().extract().response();
assertEquals(200, resp.getStatusCode());
  }

  @Test
  public void testSaveItem() {
	  Item item=new Item();
	  item.setId(3);
	  item.setChecked(true);
	  item.setDescription("sdas");
	  
	  Response  response = RestAssured.given().contentType(ContentType.JSON).header("Content-Type", ContentType.JSON)
				.body(new Gson().toJson(item)).post("http://localhost:8086/items/").then().extract().response();

	  assertEquals(200, response.getStatusCode());
  }
  @Test
  public void testupdateItem() {
	  Item item=new Item();
	  item.setId(3);
	  item.setChecked(false);
	  item.setDescription("sdsdsdas");
	  
	  Response  response = RestAssured.given().baseUri("http://localhost:8086/items/3").header("Content-Type", ContentType.JSON)
				.body(new Gson().toJson(item)).when().put().then().extract().response();
//	Response resp=(Response) RestAssured.given().when().get("http://localhost:8086/items/").then().extract().response();
	
	  assertEquals(200, response.getStatusCode());
  }
  
  @Test
  public void whenUpdatingItemItShouldUseTheGivenID() {
   
    given(repository.getOne(CHECKED_ITEM_ID)).willReturn(CHECKED_ITEM);

    controller.updateItem(NEW_ITEM, CHECKED_ITEM_ID);

    verify(repository).saveAndFlush(anyItem.capture());
   
    assertThat(anyItem.getValue().getId()).isEqualTo(CHECKED_ITEM_ID);
  }
  
  @Test
  public void whenDeletingAnItemItShouldUseTheRepository() {
 
    controller.deleteItem(CHECKED_ITEM_ID);
   
    verify(repository).delete(CHECKED_ITEM_ID);
  }
}
