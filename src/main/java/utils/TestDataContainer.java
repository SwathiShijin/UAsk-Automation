package utils;

import java.util.List;

public class TestDataContainer {
	private List<TestData> ui;
	private List<TestData> security_tests;

	public List<TestData> getUi() {
		return ui;
	}

	public void setUi(List<TestData> ui) {
		this.ui = ui;
	}
	
	public List<TestData> getSecurityTests() {
        return security_tests;
    }

    public void setSecurityTests(List<TestData> security_tests) {
        this.security_tests = security_tests;
    }
}