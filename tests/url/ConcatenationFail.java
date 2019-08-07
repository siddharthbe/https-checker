// Test case for issue 8:
// https://github.com/kelloggm/https-checker/issues/8

// @skip-test until the issue is fixed
class ConcatenationFail{
    void good() throws MalformedURLException{
        String s1 = "https";
        String s2 = "://";
        URL url = new URL(s1+s2);
    }
}