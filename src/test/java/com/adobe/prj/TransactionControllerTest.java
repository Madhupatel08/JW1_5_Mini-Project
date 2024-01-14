package com.adobe.prj;
import com.adobe.prj.api.BillController;
import com.adobe.prj.entity.Bill;
import com.adobe.prj.entity.User;
import com.adobe.prj.security.JwtHelper;
import com.adobe.prj.service.BillsService;
import com.adobe.prj.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;
import java.util.UUID;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.CoreMatchers.is;


@WebMvcTest(controllers = {BillController.class, JwtHelper.class})
@AutoConfigureMockMvc(addFilters = false)
public class TransactionControllerTest {

    @MockBean

    private BillsService billsService;

    @MockBean
    private TransactionService transactionService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtHelper jwtHelper;

    private String withUserToken(User user) {
        var token = jwtHelper.generateToken(user);
        return "Bearer " + token;
    }

    @Test
    public void addBudgetTest() throws Exception {
        User user = User.builder().username("madhu").email("madhu@gmail.com").build();
        Bill bill = Bill.builder().billId(UUID.randomUUID().toString()).billName("Groceries").user(user).billDate(new Date()).repeats("MONTHLY").minAmount(1000.00).maxAmount(1000.00).skipCount(2).build();
        ObjectMapper mapper = new ObjectMapper(); // Java <--> JSO
        String json = mapper.writeValueAsString(bill);
        when(billsService.addBill(bill)).thenReturn(bill);

        mockMvc.perform(post("/api/bills").header("Authorization", withUserToken(user))
                        .content(json).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isCreated()) // 201
                .andExpect(jsonPath("$.user.email",is("madhu@gmail.com")))
                .andExpect(jsonPath("$.user.username", is("madhu")));

        verify(billsService, times(1)).addBill(bill);
    }

    @Test
    public void updateBudgetTest() throws Exception {
        User user = User.builder().username("madhu").email("madhu@gmail.com").build();
        Bill bill = Bill.builder().billId(UUID.randomUUID().toString()).billName("Groceries").user(user).minAmount(1000.00).maxAmount(1000).billDate(new Date()).repeats("MONTHLY").skipCount(2).build();
        ObjectMapper mapper = new ObjectMapper(); // Java <--> JSON
        String json = mapper.writeValueAsString(bill);
        when(billsService.updateBill(bill)).thenReturn(bill);

        mockMvc.perform(put("/api/bills").header("Authorization", withUserToken(user))
                        .content(json).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isCreated()) // 201
                .andExpect(jsonPath("$.user.email",is("madhu@gmail.com")))
                .andExpect(jsonPath("$.user.username", is("madhu")));

    }

    @Test
    public void deleteBillTest() throws Exception {
        String res = "Bill deleted!";
        User user = User.builder().username("madhu").email("madhu@gmail.com").build();
        Bill bill = Bill.builder().billId(UUID.randomUUID().toString()).billName("Groceries").user(user).minAmount(1000.00).maxAmount(1000.00).billDate(new Date()).repeats("MONTHLY").skipCount(2).build();
        String billId = bill.getBillId();
        when(billsService.deleteBill(billId)).thenReturn(res);
        mockMvc.perform(delete("/api/bills/"+billId).header("Authorization", withUserToken(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(jsonPath("$", is(res))); // 200

        verify(billsService, times(1)).deleteBill(bill.getBillId());
    }

    @Test
    public void getbillTest() throws Exception{

    }
}