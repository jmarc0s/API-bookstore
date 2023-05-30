package br.com.jmarcos.bookstore.storehouse;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import br.com.jmarcos.bookstore.controller.dto.storehouse.StorehouseUpdateDTO;
import br.com.jmarcos.bookstore.model.Address;
import br.com.jmarcos.bookstore.model.Storehouse;
import br.com.jmarcos.bookstore.repository.StorehouseRepository;
import br.com.jmarcos.bookstore.service.StorehouseService;
import br.com.jmarcos.bookstore.service.exceptions.ConflictException;
import br.com.jmarcos.bookstore.service.exceptions.ResourceNotFoundException;

@ExtendWith(MockitoExtension.class)
public class StorehouseServiceTest {
    @InjectMocks
    private StorehouseService storehouseService;

    @Mock
    private StorehouseRepository storehouseRepository;

    @Test
    void test() {
        Assertions.assertTrue(true);
    }

    @Test
    void save_returns_ASavedStorehouse_WhenSuccessful() {
        Storehouse storehouse = createStorehouse();
        when(storehouseRepository.save(any(Storehouse.class))).thenReturn(storehouse);

        Storehouse savedStorehouse = storehouseService.save(storehouse);

        Assertions.assertNotNull(savedStorehouse);
        Assertions.assertNotNull(savedStorehouse.getId());
        Assertions.assertEquals(storehouse.getCode(), savedStorehouse.getCode());
        Assertions.assertEquals(storehouse.getPhone(), savedStorehouse.getPhone());
        Assertions.assertEquals(storehouse.getAddress(), savedStorehouse.getAddress());
        Assertions.assertTrue(storehouse.getBookList().isEmpty());
        verify(storehouseRepository).save(storehouse);

    }

    @Test
    void save_Throws_ConflictException_WhenStorehouseCodeIsAlreadyInUse() {
        Storehouse storehouse = createStorehouse();
        Storehouse newStorehouse = createStorehouse();
        when(storehouseRepository.findByCode(anyInt())).thenReturn(Optional.of(storehouse));

        ConflictException conflictExceptionException = Assertions
                .assertThrows(ConflictException.class,
                        () -> storehouseService.save(newStorehouse));

        Assertions.assertTrue(conflictExceptionException.getMessage()
                .contains("Storehouse code is already in use."));

    }

    @Test
    void search_returns_AllStorehouses_WhenSuccessful() {
        PageRequest pageable = PageRequest.of(0, 5);
        List<Storehouse> storehouseList = List.of(createStorehouse());
        PageImpl<Storehouse> storehousePage = new PageImpl<>(storehouseList);

        when(storehouseRepository.findAll(pageable)).thenReturn(storehousePage);

        Page<Storehouse> all = storehouseService.search(pageable);
        List<Storehouse> storehousesSavedList = all.stream().toList();

        Assertions.assertFalse(storehousesSavedList.isEmpty());
        Assertions.assertEquals(storehouseList.get(0).getCode(), storehousesSavedList.get(0).getCode());
        Assertions.assertEquals(storehouseList.get(0).getPhone(), storehousesSavedList.get(0).getPhone());
        Assertions.assertEquals(storehouseList.get(0).getAddress(), storehousesSavedList.get(0).getAddress());
        Assertions.assertTrue(storehousesSavedList.get(0).getBookList().isEmpty());
        Assertions.assertNotNull(storehousesSavedList.get(0).getId());

        verify(storehouseRepository).findAll(pageable);

    }

    @Test
    void searchByID_returns_AStorehouseTheGivenId_WhenSuccessful() {
        Storehouse storehouse = createStorehouse();
        when(storehouseRepository.findById(storehouse.getId())).thenReturn(Optional.of(storehouse));

        Storehouse returnedStorehouse = this.storehouseService.searchByID(storehouse.getId());

        Assertions.assertNotNull(returnedStorehouse);
        Assertions.assertEquals(storehouse.getId(), returnedStorehouse.getId());
        Assertions.assertEquals(storehouse.getCode(), returnedStorehouse.getCode());
        Assertions.assertEquals(storehouse.getPhone(), returnedStorehouse.getPhone());
        Assertions.assertEquals(storehouse.getAddress(), returnedStorehouse.getAddress());
        Assertions.assertTrue(storehouse.getBookList().isEmpty());
        verify(storehouseRepository).findById(storehouse.getId());
    }

    @Test
    void searchByID_Throws_ResourceNotFoundException_WhenStorehouseNotFound() {
        when(storehouseRepository.findById(anyLong())).thenReturn(Optional.empty());

        ResourceNotFoundException resourceNotFoundException = Assertions
                .assertThrows(ResourceNotFoundException.class,
                        () -> storehouseService.searchByID(1L));


            Assertions.assertTrue(resourceNotFoundException.getMessage()
                .contains("Storehouse not found with the given id"));
        
    }

    @Test
    void searchByCode_Throws_ResourceNotFoundException_WhenStorehouseNotFound() {
        when(storehouseRepository.findByCode(anyInt())).thenReturn(Optional.empty());

        ResourceNotFoundException resourceNotFoundException = Assertions
                .assertThrows(ResourceNotFoundException.class,
                        () -> storehouseService.searchByCode(1));


            Assertions.assertTrue(resourceNotFoundException.getMessage()
                .contains("Storehouse not found with the given code"));
        
    }

    @Test
    void delete_returns_True_WhenSuccessful() {
        Storehouse storehouse = createStorehouse();
        when(storehouseRepository.findById(anyLong())).thenReturn(Optional.of(storehouse));

        boolean result = this.storehouseService.delete(storehouse.getId());

        Assertions.assertTrue(result);
        verify(storehouseRepository).delete(storehouse);
        verify(storehouseRepository).findById(storehouse.getId());
    }

    @Test
    void delete_returns_False_WhenStorehouseNotFound() {
        when(storehouseRepository.findById(anyLong())).thenReturn(Optional.empty());

        boolean result = this.storehouseService.delete(1L);

        Assertions.assertFalse(result);
        verify(storehouseRepository, never()).delete(any(Storehouse.class));
        verify(storehouseRepository).findById(1L);
    }

    @Test
    void update_returns_AUpdatedStorehouse_WhenSuccessful() {
        Storehouse storehouse = createStorehouse();
        StorehouseUpdateDTO storehouseUpdateDTO = createStorehouseUpdateDTO();
        when(storehouseRepository.save(storehouse)).thenReturn(storehouse);
        when(storehouseRepository.findById(anyLong())).thenReturn(Optional.of(storehouse));

        Storehouse updatedStorehouse = storehouseService.update(storehouseUpdateDTO.toStorehouse(1L));

        Assertions.assertNotNull(updatedStorehouse);
        Assertions.assertEquals(storehouse.getId(), updatedStorehouse.getId());
        Assertions.assertEquals(storehouseUpdateDTO.getPhone(), updatedStorehouse.getPhone());
        verify(storehouseRepository).save(storehouse);

    }

    Storehouse createStorehouse() {
        Storehouse storehouse = new Storehouse();
        storehouse.setCode(1);
        storehouse.setPhone("999999");
        storehouse.setAddress(new Address("rua a", 111, "caninde", "CE", "627000000"));
        storehouse.getAddress().setId(1L);
        storehouse.setId(1L);
        return storehouse;
    }

    private StorehouseUpdateDTO createStorehouseUpdateDTO() {
        StorehouseUpdateDTO storehouseUpdateDTO = new StorehouseUpdateDTO();

        storehouseUpdateDTO.setPhone("11111111");

        return storehouseUpdateDTO;
    }
}
