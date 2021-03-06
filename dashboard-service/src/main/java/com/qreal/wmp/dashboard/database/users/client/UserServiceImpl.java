package com.qreal.wmp.dashboard.database.users.client;

import com.qreal.wmp.dashboard.database.exceptions.AbortedException;
import com.qreal.wmp.dashboard.database.exceptions.ErrorConnectionException;
import com.qreal.wmp.dashboard.database.exceptions.NotFoundException;
import com.qreal.wmp.dashboard.database.users.model.User;
import com.qreal.wmp.thrift.gen.TUser;
import com.qreal.wmp.thrift.gen.UserDbService;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;

/** Thrift client side of UserDBService.*/
@Service("userService")
@PropertySource("classpath:client.properties")
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private TTransport transport;

    private UserDbService.Client client;

    @Value("${port.db.user}")
    private int port;

    @Value("${path.db.user}")
    private String url;

    /** Creates connection with Thrift TServer.*/
    @PostConstruct
    public void start() {
        logger.info("Client UserService was created with Thrift socket on url = {}, port = {}", url, port);
        transport = new TSocket(url, port);
        TProtocol protocol = new TBinaryProtocol(transport);
        client = new UserDbService.Client(protocol);
    }

    @Override
    @Transactional
    public void save(@NotNull User user) throws AbortedException, ErrorConnectionException, TException {
        TUser tUser = user.toTUser();
        logger.trace("save() was called with parameters: user = {}.", user.getUsername());
        transport.open();
        try {
            client.save(tUser);
        } finally {
            transport.close();
        }
        logger.trace("save() successfully saved {} user.", user.getUsername());
    }

    @Override
    @Transactional
    public void update(@NotNull User user) throws AbortedException, ErrorConnectionException, TException {
        TUser tUser = user.toTUser();
        logger.trace("update() was called with parameters: user = {}", user.getUsername());
        transport.open();
        try {
            client.update(tUser);
        } finally {
            transport.close();
        }
        logger.trace("update() successfully updated {} user.", user.getUsername());
    }

    @Override
    @Transactional
    @NotNull
    public User findByUserName(String username) throws NotFoundException, ErrorConnectionException, TException {
        logger.trace("findByUserName() was called with parameters: username = {}.", username);
        TUser tUser = null;
        transport.open();
        try {
            tUser = client.findByUserName(username);
        } finally {
            transport.close();
        }
        logger.trace("findByUserName() successfully returned an answer.");
        return new User(tUser);
    }

    @Override
    @Transactional
    public boolean isUserExist(String username) throws ErrorConnectionException, TException {
        logger.trace("isUserExist() was called with parameters: username = {}", username);
        boolean isUserExist = false;
        transport.open();
        try {
            isUserExist = client.isUserExist(username);
        } finally {
            transport.close();
        }
        logger.trace("isUserExist() successfully returned an answer");
        return isUserExist;
    }
}
