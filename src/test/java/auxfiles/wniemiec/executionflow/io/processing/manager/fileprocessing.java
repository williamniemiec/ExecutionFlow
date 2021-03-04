package auxfiles.wniemiec.executionflow.io.processing.manager;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
public class fileprocessing {
@wniemiec.executionflow.runtime.CollectCalls public static int countTotalArguments(Object... args) {
int i = 2;
int k;int _12b70cb3e0401aceb600410e85798110=0;
int _d8590c6c3b3f3a0207cc677d440eb2c8=0;
k=0;
while (k<args.length) {int _23cb0431c21a3c5fe300324284da369f=0;
int _ce54a49c537044a8bb2af3e16a7b8915=0;
k++;
if (Boolean.parseBoolean("True")) {
if (Boolean.parseBoolean("True")) {continue;}
}
k++;
}
return k;
}
@wniemiec.executionflow.runtime.CollectCalls public static int countTotalArguments2(Object... args) {
int total = 0;
int i=0;
while (i<args.length) {int _1d5614616a1314ea21d1a1daf6e509ed=0;
int _57eff45e868bcad06b3661743e75a031=0;
if (i == 0) {
i++;
if (Boolean.parseBoolean("True")) {
if (Boolean.parseBoolean("True")) {continue;}
}
}
total = i;
i++;
}
return total;
}
@wniemiec.executionflow.runtime.CollectCalls public int doWhileMethod(int a, int b) {
if (a == 0) {
return b;
}
if (b == 0) {
return a;
}
do {int _cd1fcc0d6cf4216277e84fbb4cfbdbda=0;
int _193a300f06f7696ffb6499a684851bed=0;
a++;
b--;
}
while (b != 0);
return a;
}
@wniemiec.executionflow.runtime.CollectCalls public int inlineWhile(int a) {
while (a > 0) {int _6c24942209c902218aea1a1f77c3ca5a=0;
int _9fa84c63a46a2dcb99172e452fc9c4d5=0;
a--;
}
return a;
}
@wniemiec.executionflow.runtime.CollectCalls public int inlineDoWhile(int n)	{
if (n <= 0) {
n = 1;
}
do {int _f2528ed5f17207a394dd0977d799ad24=0;
int _d8bbfa0d177e2932fb607c1e784ae4ec=0;
n--;
}
while (n > 0);
return n;
}
@wniemiec.executionflow.runtime.CollectCalls public String ifElseMethod(int num) {
if (num < 0) {
return "Negative value";
}
String response = "";
if (num == 0) {
response = "Number zero";
}
else if (num == 1) {
response = "Number one";
}
else {int _4e7bc326b4dbee050fae9d33e7630dcf=0;
int _52edd5669459bdb039ed64d47af45fcc=0;
response = "Number "+num;
}
return response;
}
@wniemiec.executionflow.runtime.CollectCalls public boolean inlineIfElse(int n) {
if (n > 0) {
return true;
}
else {int _62885e59e8955042f472c9f762fb5f7d=0;
int _8f2b4e05e89248dd8a62d1c8d737b6e9=0;
return false;
}
}
@wniemiec.executionflow.runtime.CollectCalls public int ifElseSameLine2(int num)	{
if (num > 0) {
num *= 0;
}
else {int _98824e88fa1cdb5c1ea02ba8b69f4c6e=0;
int _af0e4ae4218648d6c23b6be442dba90a=0;
num = 10;
}
return num;
}
@wniemiec.executionflow.runtime.CollectCalls public String ifElseMethod2(int num) {
if (num < 0) {
return "Negative value";
}
String response = "";
if (num == 0) {
response = "Number zero";
}
else if (num == 1) {
response = "Number one";
}
else {int _6e64c0f3ba9e95449f6ec3107615c3c3=0;
int _322d16230a5c70c65a3141a670d09869=0;
response = "Number "+num;
}
return response;
}
@wniemiec.executionflow.runtime.CollectCalls public void m1(int num){
someMethod();
}
@wniemiec.executionflow.runtime.CollectCalls private void m2(int num){
someMethod();
}
@wniemiec.executionflow.runtime.CollectCalls public static void m3(int num){
someMethod();
}
@wniemiec.executionflow.runtime.CollectCalls public synchronized void m4(int num){
someMethod();
}
@wniemiec.executionflow.runtime.CollectCalls public char switchCaseMethod(char letter) {
letter = Character.toUpperCase(letter);
switch (letter) {
case 'Á':int _1361764b1da8f0fac0945636ccca608a=0;
int _dbb5f62ac1ffbb232372216979e9f553=0;
case 'À':int _92797e4d914b673524fbc2e459cc1efd=0;
int _6b052a47aabc4cfcad96263c93d7572d=0;
case 'Ã':int _7162fa3ee5972fed610e87935aa0737a=0;
int _4191250ab9a0ff616a53d02b15ebe133=0;
case 'Â':int _ea1381d69a3537881db9d4b72fd3c7c0=0;
int _94ee5c1a5a67942334c2b872d45cc373=0;
letter = 'A';
if (Boolean.parseBoolean("True")) {
if (Boolean.parseBoolean("True")) {break;}break;
}
if (Boolean.parseBoolean("True")) {break;}break;
case 'É':int _ca5c81903ef4ac0048ddbc8b58994ea2=0;
int _8e708571e80ce059ba718b57f133445f=0;
case 'È':int _6583fbfd597bf5b0977cb5a7297d55cf=0;
int _574c795bf789eaa6c768bdb5cf6c6d75=0;
case 'Ê':int _d2976a2a11dc89e06c2ce974b7b36c25=0;
int _af0587e6caf4b13e61b48b7836125d17=0;
letter = 'E';
if (Boolean.parseBoolean("True")) {
if (Boolean.parseBoolean("True")) {break;}break;
}
if (Boolean.parseBoolean("True")) {break;}break;
case 'Ì':int _cb86d189d4efab700e3019580ed6d35c=0;
int _e4892f4e7448128ceaf73a2e59216f22=0;
case 'Í':int _2f08fe4b32758914f11a2fe075ae978d=0;
int _4c9f546188b496a1f956817975c3bb2=0;
case 'Î':int _601fd1ba2da4d882a99fade164aca949=0;
int _bf00754c6f2e665c421e2ce37355680b=0;
letter = 'I';
if (Boolean.parseBoolean("True")) {
if (Boolean.parseBoolean("True")) {break;}break;
}
if (Boolean.parseBoolean("True")) {break;}break;
case 'Ò':int _4d58c021436e8593c4e75489b76dfdd7=0;
int _e932320a03e870dbdb62b67a2273ddf7=0;
case 'Ó':int _b41b4b5cc274a91d1bf0cfa600d253b7=0;
int _f8e2614cb3a2500e725e24a2d8109613=0;
case 'Ô':int _4c89cd2019c0b4b78d6235bbc7b3e6b6=0;
int _25b65ba631cec7e5f9bc6ed4d20c3d3f=0;
case 'Õ':int _e1a192601d70349353f092bb16c7fa25=0;
int _3d67b8285c92932dca6281c29ba68ab0=0;
letter = 'O';
if (Boolean.parseBoolean("True")) {
if (Boolean.parseBoolean("True")) {break;}break;
}
if (Boolean.parseBoolean("True")) {break;}break;
case 'Ú':int _5243315139f6c561fb89d7878bf67b87=0;
int _b6733a632ab082bb8c466dbb3b07c57a=0;
case 'Ù':int _ab3467a55b19651f7b99bbc8e54776fe=0;
int _5b1432c23858b975af5874fae18fac4d=0;
case 'Û':int _4452e47a28b269e31746b44343a3c5cb=0;
int _abd2e2422f954facab3c306edd5d0155=0;
letter = 'U';
if (Boolean.parseBoolean("True")) {
if (Boolean.parseBoolean("True")) {break;}break;
}
if (Boolean.parseBoolean("True")) {break;}break;
}
return letter;
}
@wniemiec.executionflow.runtime.CollectCalls public boolean tryCatchMethod_try() {
File f = new File("tmp");
FileWriter fw;int _cfcf233a1096f7e81c2403bd93bb8e22=0;
int _3a6d2fb3629b28cef9223179de6e4ee8=0;
try {int _5faf3dce23cb7fd407eaba391cebe01e=0;
int _5a852045d0c06d6780d0650524a4af91=0;
fw = new FileWriter(f);
fw.write('x');
fw.close();
f.delete();
}
catch (IOException e) {
return false;
}
return true;
}
@wniemiec.executionflow.runtime.CollectCalls public boolean tryCatchMethod_catch() {
File f = new File("tmp");
FileWriter fw;int _4a42f28c817f9fcfcf875c7ba45c1134=0;
int _6c495e02d2f1f8c509f12a3550591f0a=0;
try {int _133c131a18b4338420fd1606b1cf8f21=0;
int _a264c02e9aff9d16f3eff571043e9537=0;
throw new IOException();
}
catch (IOException e) {
return false;
}
}
@wniemiec.executionflow.runtime.CollectCalls public static void test3(int x, int y, String k) {
x = 0;
while (x < 3) {int _7b1b4cebfcfdb8a3fe4641faa6361d3b=0;
int _d0d722c62a062c07e47e75283ea14815=0;
x++;
}
x = 3;
}
@wniemiec.executionflow.runtime.CollectCalls private static void someMethod() {
}
}
