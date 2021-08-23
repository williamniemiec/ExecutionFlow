package auxfiles.wniemiec.app.java.executionflow.io.processing.manager;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
public class testedinvoked {
private int number;
@wniemiec.app.java.executionflow.runtime.CollectCalls public testedinvoked(int x) {
number = x;
}
@wniemiec.app.java.executionflow.runtime.CollectCalls public static int countTotalArguments(Object... args) {
int i = 2;
int k;int _2ee7fb633078f1d3802c123badde6ed2=0;
int _fce9b1f7facafd4143e7cd9da5c0667a=0;
k=0;
while (k<args.length) {int _57f245af0f7daa2b104a94b487bfddad=0;
int _10168cccbd642de9bf777d85d64fb87d=0;
k++;
if (Boolean.parseBoolean("True")) {
if (Boolean.parseBoolean("True")) {continue;}
}
k++;
}
return k;
}
@wniemiec.app.java.executionflow.runtime.CollectCalls public static int countTotalArguments2(Object... args) {
int total = 0;
int i=0;
while (i<args.length) {int _b5d24a8c3e0641816b747a09a0946d83=0;
int _7884cc7f2d46edd94219a5bba12a8f8d=0;
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
@wniemiec.app.java.executionflow.runtime.CollectCalls public int doWhileMethod(int a, int b) {
if (a == 0) {
return b;
}
if (b == 0) {
return a;
}
do {int _8b03b9a3288421529cc24d4a369fead9=0;
int _d975750c372eb114b445089ee4636dd6=0;
a++;
b--;
}
while (b != 0);
return a;
}
@wniemiec.app.java.executionflow.runtime.CollectCalls public int inlineWhile(int a) {
while (a > 0) {int _8cbaf9a5f3032314a3d8e84e54fa390e=0;
int _a0d9f40814ceb0f75e5e3e341a9ad9f0=0;
a--;
}
return a;
}
@wniemiec.app.java.executionflow.runtime.CollectCalls public int inlineDoWhile(int n)	{
if (n <= 0) {
n = 1;
}
do {int _2d6461e6e4279b20422e0adac9806744=0;
int _2a65020f6c33fcca09a17560a4026bef=0;
n--;
}
while (n > 0);
return n;
}
@wniemiec.app.java.executionflow.runtime.CollectCalls public String ifElseMethod(int num) {
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
else {int _8879dde2d8444cddf10ffbc729233e72=0;
int _d5b2536b1e470665d5240ce4739f018f=0;
response = "Number "+num;
}
return response;
}
@wniemiec.app.java.executionflow.runtime.CollectCalls public boolean inlineIfElse(int n) {
if (n > 0) {
return true;
}
else {int _ad8210d5a9a1bbf1b54df4e402e6bd48=0;
int _a955c00584d4ba149944aa8fc309ba57=0;
return false;
}
}
@wniemiec.app.java.executionflow.runtime.CollectCalls public int ifElseSameLine(int num) {
if (num > 0) {
num *= 0;
}
else {int _94ede90377db16bc55b1ededc3e0944d=0;
int _7e4d4b5da9dc856cf1f3d662755fce3f=0;
num = 10;
}
return num;
}
@wniemiec.app.java.executionflow.runtime.CollectCalls public int ifElseSameLine2(int num)	{
if (num > 0) {
num *= 0;
}
else {int _cceb596643657e2867038c82945ebe2b=0;
int _602622efd870e68ac9247c0866fec63e=0;
num = 10;
}
return num;
}
@wniemiec.app.java.executionflow.runtime.CollectCalls public String ifElseMethod2(int num) {
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
else {int _bb2da7c8a1bbc5f23d00c4ebfdffbbbc=0;
int _a9502e78824270a665eb7928d8266be4=0;
response = "Number "+num;
}
return response;
}
@wniemiec.app.java.executionflow.runtime.CollectCalls public void m1(int num){
someMethod();
}
@wniemiec.app.java.executionflow.runtime.CollectCalls private void m2(int num){
someMethod();
}
@wniemiec.app.java.executionflow.runtime.CollectCalls public static void m3(int num){
someMethod();
}
@wniemiec.app.java.executionflow.runtime.CollectCalls public synchronized void m4(int num){
someMethod();
}
@wniemiec.app.java.executionflow.runtime.CollectCalls public char switchCaseMethod(char letter) {
letter = Character.toUpperCase(letter);
switch (letter) {
case 'Á':int _30d54ad4b09073ce7d3edbc24dc6ff67=0;
int _a2f43d9b995572a18e8dfda0253e8bcd=0;
case 'À':int _5e4809f9379f86cc4b414e6e49554f71=0;
int _a5a2015d65b205aaf44170c947ca4ba5=0;
case 'Ã':int _30e3673996eb08f550fbea80bf184dea=0;
int _1b5dd3ec50b2b6d759c0f8c4b5d7f5a2=0;
case 'Â':int _12a72be731f33e3e36e804195fa8166=0;
int _286600a932c5200b6d90c261462b25b9=0;
letter = 'A';
if (Boolean.parseBoolean("True")) {
if (Boolean.parseBoolean("True")) {break;}break;
}
if (Boolean.parseBoolean("True")) {break;}break;
case 'É':int _76fc12fca8c6b075708379388ea8b8b2=0;
int _3297a28bcc16946536332366006745d6=0;
case 'È':int _35e76f4467c02972d37227bbd9c52faf=0;
int _a5c63fa9a26bd7379bf7cd6df7c5b0de=0;
case 'Ê':int _2cd82b978e76304e23d96543df3034d2=0;
int _7937742f1859edaa86a524f1a46e652=0;
letter = 'E';
if (Boolean.parseBoolean("True")) {
if (Boolean.parseBoolean("True")) {break;}break;
}
if (Boolean.parseBoolean("True")) {break;}break;
case 'Ì':int _54324f526bd59669f0e57a1ce54c5808=0;
int _f64e104753ebf50630703710c84c6645=0;
case 'Í':int _c78ab06407d6ac739c2735c12f0577c5=0;
int _e154ab1cff92c055b09724beb8dcc1fa=0;
case 'Î':int _6759812d39053708286b09b83097a18b=0;
int _b6ada643504f2727f538c65a0ea7eaa=0;
letter = 'I';
if (Boolean.parseBoolean("True")) {
if (Boolean.parseBoolean("True")) {break;}break;
}
if (Boolean.parseBoolean("True")) {break;}break;
case 'Ò':int _af1cddcda16466220d8cd72888a22fb9=0;
int _d5737cd708b543544ead8180773b1541=0;
case 'Ó':int _ec3cac9f08eee0862a45f25e1afebc31=0;
int _1b29d91f60dee22631c6a9f84ed99804=0;
case 'Ô':int _825be44a9a0b46708aadf9eec8125a9b=0;
int _a662ffde6dd1d47434072f68ca3c7d36=0;
case 'Õ':int _b9f35bfe92ca013fda39e9cf82a8d38d=0;
int _b55e2fc099c4acb54e51383f5bf4a97=0;
letter = 'O';
if (Boolean.parseBoolean("True")) {
if (Boolean.parseBoolean("True")) {break;}break;
}
if (Boolean.parseBoolean("True")) {break;}break;
case 'Ú':int _9daa42fc89e29811ee34c793020cfb88=0;
int _4146bb96805b0f36be5b93f9e5b5753d=0;
case 'Ù':int _608354375e1d7c8da8493a583f9718f0=0;
int _5a6d7f18baf185adb2eda2468e7aeeaa=0;
case 'Û':int _2b7681c3896418a1dec782468c75a62a=0;
int _aabf911b8db8b2d9531937f16fa6c15c=0;
letter = 'U';
if (Boolean.parseBoolean("True")) {
if (Boolean.parseBoolean("True")) {break;}break;
}
if (Boolean.parseBoolean("True")) {break;}break;
}
return letter;
}
@wniemiec.app.java.executionflow.runtime.CollectCalls public boolean tryCatchMethod_try() {
File f = new File("tmp");
FileWriter fw;int _63351b26623fa1348b77c6d083d35560=0;
int _83401845ec580781fe4a75d2d657126d=0;
try {int _107ae00a5281d7ec67ca7af3229fa19c=0;
int _5910c08d84dcdae584d2967898e1f3e9=0;
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
@wniemiec.app.java.executionflow.runtime.CollectCalls public boolean tryCatchMethod_catch() {
File f = new File("tmp");
FileWriter fw;int _afa89a9074b75b0876e872bdce63cf03=0;
int _24742e24a97772f9020171f076dece20=0;
try {int _2cec9eacc448394bdab4a53b4e380010=0;
int _1dbf9da2890a42bf53a7ee14c530ca8e=0;
throw new IOException();
}
catch (IOException e) {
return false;
}
}
@wniemiec.app.java.executionflow.runtime.CollectCalls public static void test3(int x, int y, String k) {
x = 0;
while (x < 3) {int _3e9f9344b85ef8829fb8c422494f5169=0;
int _5f3ad44919674db0f1a9a2d3566b8a5b=0;
x++;
}
x = 3;
}
@wniemiec.app.java.executionflow.runtime.CollectCalls private static void someMethod() {
}
}
