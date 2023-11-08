package com.picpayproject.services;

import com.picpayproject.domain.transaction.Transaction;
import com.picpayproject.domain.user.User;
import com.picpayproject.dtos.TransactionDTO;
import com.picpayproject.repositories.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Service
public class TransactionService {
    @Autowired
    private UserService userService;
    @Autowired
    private TransactionRepository repository;
    @Autowired
    private AuthorizationService authService;
    @Autowired
    private NotificationService notificationService;

    public Transaction createTransaction(TransactionDTO transaction) throws Exception {
        User sender = this.userService.findUserById(transaction.senderId());
        User receiver = this.userService.findUserById(transaction.receiverId());

        userService.validateTransaction(sender, transaction.value());

        /*
        Todo o processo de transação entre um usuario e outro, gerido pelo microserviço sugerido pelo picpay
        aqui ele verifica se a transação esta autorizada ou não
        * */
        boolean isAuthorized = this.authService.authorizeTransaction(sender, transaction.value());
        if (!isAuthorized) {
            throw new Exception("Transação não autorizada\n " + "Unauthorized transaction");
        }

        /*
        aqui é criada a transaction onde todos os pontos solicitados sao inseriods como, pagante, recebedor, e valor
        de transferencia
        * */
        Transaction newTransaction = new Transaction();
        newTransaction.setAmount(transaction.value());
        newTransaction.setSender(sender);
        newTransaction.setReceiver(receiver);
        newTransaction.setTimeStamp(LocalDateTime.now());

        /*
        aqui é feita a atualização dos valores de cada conta, tanto da que enviou quanto da que recebeu
        é subtraido do usuario "sender" e adicionado no usuario "receiver"
        * */
        sender.setBalance(sender.getBalance().subtract(transaction.value()));
        receiver.setBalance(receiver.getBalance().add(transaction.value()));

        /*
        aqui é feita a persistencia dessa transação dentro do nosso banco de dados e atualizo os usuarios
        * */
        this.repository.save(newTransaction);
        this.userService.saveUser(sender);
        this.userService.saveUser(receiver);

        /*
        por fim é enviada uma notificação de transferencia realizada e valor recebido com sucesso
        * */
        this.notificationService.sendNotification(sender,
                "Transação realizada com sucesso\n" + "Transaction successfully completed");
        this.notificationService.sendNotification(receiver,
                "Transação recebida com sucesso\n" + "Transaction received successfully");

        return newTransaction;

    }


}
