package com.example.hello;

import java.time.Duration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.virtualauthenticator.VirtualAuthenticator;
import org.openqa.selenium.virtualauthenticator.VirtualAuthenticatorOptions;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;

@Import(TestcontainersConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, properties = { "server.port=34321",
		"webauthn.allowed-origins=http://localhost:34321", "spring.docker.compose.enabled=false" })
class HelloPasskeysApplicationTests {

	private static ChromeDriver driver;

	private static VirtualAuthenticator authenticator;

	private static WebDriverWait wait;

	@BeforeAll
	static void setupClass() {
		ChromeOptions options = new ChromeOptions();
		options.addArguments("--headless=new");
		driver = new ChromeDriver(options);
		VirtualAuthenticatorOptions authenticatorOptions = new VirtualAuthenticatorOptions().setIsUserVerified(true)
			.setIsUserConsenting(true)
			.setHasUserVerification(true)
			.setHasResidentKey(true)
			.setTransport(VirtualAuthenticatorOptions.Transport.INTERNAL)
			.setProtocol(VirtualAuthenticatorOptions.Protocol.CTAP2);
		authenticator = driver.addVirtualAuthenticator(authenticatorOptions);
		wait = new WebDriverWait(driver, Duration.ofSeconds(5));
	}

	@AfterAll
	static void teardownClass() {
		if (driver != null) {
			driver.quit();
		}
	}

	@Test
	void testPasskeyFlow() {
		// Step 1: Initial login with username/password
		driver.get("http://localhost:34321");
		// Wait for redirect to login page
		wait.until(ExpectedConditions.urlContains("/login"));
		// Fill in login form
		wait.until(ExpectedConditions.presenceOfElementLocated(By.id("username")));
		driver.findElement(By.id("username")).sendKeys("user@example.com");
		driver.findElement(By.id("password")).sendKeys("password");
		driver.findElement(By.cssSelector("form.login-form button[type='submit']")).click();
		// Wait for successful login
		wait.until(ExpectedConditions.urlToBe("http://localhost:34321/"));
		assertThat(driver.findElement(By.id("hello")).getText()).isEqualTo("Hello user@example.com!");

		// Step 2: Register a passkey
		driver.get("http://localhost:34321/passkeys");
		// Wait for registration form
		driver.findElement(By.cssSelector("button.add-button")).click();
		wait.until(ExpectedConditions.presenceOfElementLocated(By.id("passkeyName")));
		// Enter passkey label
		driver.findElement(By.id("passkeyName")).sendKeys("My Test Passkey");
		// Click register button and wait for WebAuthn ceremony
		driver.findElement(By.id("add-passkey")).click();
		// Check credentials were created - should be exactly 1
		wait.until(driver -> authenticator.getCredentials().size() == 1);
		assertThat(authenticator.getCredentials()).hasSize(1);

		// Step 3: Logout
		driver.get("http://localhost:34321/logout");
		// Wait for logout confirmation page
		wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("button[type='submit']")));
		assertThat(driver.findElement(By.tagName("body")).getText()).contains("Are you sure you want to log out?");
		// Confirm logout
		driver.findElement(By.cssSelector("button[type='submit']")).click();
		// Wait for redirect to login page
		wait.until(ExpectedConditions.urlContains("/login"));

		// Step 4: Login with passkey
		wait.until(ExpectedConditions.presenceOfElementLocated(By.id("passkey-signin")));
		// Click "Sign in with a passkey" button
		WebElement passkeyButton = driver.findElement(By.id("passkey-signin"));
		assertThat(passkeyButton.isDisplayed()).isTrue();
		passkeyButton.click();
		// Check if login was successful
		wait.until(ExpectedConditions.urlToBe("http://localhost:34321/"));
		assertThat(driver.findElement(By.id("hello")).getText()).isEqualTo("Hello user@example.com!");
	}

}
