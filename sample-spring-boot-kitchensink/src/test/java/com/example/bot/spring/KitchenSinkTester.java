package com.example.bot.spring;


import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.InputStream;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.WebApplicationContext;

import com.google.common.io.ByteStreams;

import com.linecorp.bot.client.LineMessagingClient;
import com.linecorp.bot.model.ReplyMessage;
import com.linecorp.bot.model.event.Event;
import com.linecorp.bot.model.event.FollowEvent;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.message.MessageContent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.spring.boot.annotation.LineBotMessages;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import com.example.bot.spring.DatabaseEngine;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = { KitchenSinkTester.class, DatabaseEngine.class })
public class KitchenSinkTester {
	@Autowired
	private DatabaseEngine databaseEngine;
	
	@Test
	public void testNotFound() throws Exception {
		boolean thrown = false;
		try {
			this.databaseEngine.search("no");
		} catch (Exception e) {
			thrown = true;
		}
		assertThat(thrown);
	}
	
	@Test
	public void testFound() throws Exception {
		boolean thrown = false;
		String result = null;
		String random = "dafkjckdieu";
				
		try {
			result = this.databaseEngine.search(random + "abc" + random);
		} catch (Exception e) {
			thrown = true;
		}
		assertThat(!thrown);
		assertThat(result.equals("def"));
		
		try {
			result = this.databaseEngine.search(random + "Hi" + random);
		} catch (Exception e) {
			thrown = true;
		}
		
		assertThat(!thrown);
		assertThat(result.equals("Hey, how things going?"));
		
		try {
			result = this.databaseEngine.search(random + "I am fine" + random);
		} catch (Exception e) {
			thrown = true;
		}
		
		assertThat(!thrown);
		assertThat(result.equals("Great!"));
		
		try {
			result = this.databaseEngine.search(random + "Who is Prof Kim" + random);
		} catch (Exception e) {
			thrown = true;
		}
		
		assertThat(!thrown);
		assertThat(result.equals("Well, this is your instructor."));
		
		try {
			result = this.databaseEngine.search(random + "How is the grade of this course?" + random);
		} catch (Exception e) {
			thrown = true;
		}
		
		assertThat(!thrown);
		assertThat(result.equals("This is absolute good grade for good student. And I am sure you are!"));
	}
}
