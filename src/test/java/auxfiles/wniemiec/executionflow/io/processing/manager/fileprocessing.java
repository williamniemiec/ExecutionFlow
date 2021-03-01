package auxfiles.wniemiec.executionflow.io.processing.manager;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
public class fileprocessing {
@wniemiec.executionflow.runtime.CollectCalls public static int countTotalArguments(Object... args) {
int i = 2;
int k;int _ceb2af37f2ac28501bf1e94c53031f77=0;
k=0;
while (k<args.length) {int _7cf80896298d2c2200bb2fff1cb4472=0;
k++;
if (Boolean.parseBoolean("True")) {continue;}
k++;
}
return k;
}
@wniemiec.executionflow.runtime.CollectCalls public static int countTotalArguments2(Object... args) {
int total = 0;
int i=0;
while (i<args.length) {int _21e2c707cf03d67a64255c4d25cab60c=0;
if (i == 0) {
i++;
if (Boolean.parseBoolean("True")) {continue;}
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
do {int _a98b8584ab7bc02f91dd9fdc385c11c1=0;
a++;
b--;
}
while (b != 0);
return a;
}
@wniemiec.executionflow.runtime.CollectCalls public int inlineWhile(int a) {
while (a > 0) {int _18a5505fc7c16bb5d11bf6d2e8a48d4a=0;
a--;
}
return a;
}
@wniemiec.executionflow.runtime.CollectCalls public int inlineDoWhile(int n)	{
if (n <= 0) {
n = 1;
}
do {int _6a2090a24d62c2f94c7e081b427497c2=0;
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
else {int _3095e02121bc66c6d90a0209b9916733=0;
response = "Number "+num;
}
return response;
}
@wniemiec.executionflow.runtime.CollectCalls public boolean inlineIfElse(int n) {
if (n > 0) {
return true;
}
else {int _123d9350bbdb959677e694ceeb7b356=0;
return false;
}
}
@wniemiec.executionflow.runtime.CollectCalls public int ifElseSameLine2(int num)	{
if (num > 0) {
num *= 0;
}
else {int _8b65b8b98c73b4064ddfcd9d407d8268=0;
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
else {int _3472bad966203fe2cb71f6cfb83f7cea=0;
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
case 'Á':int _53a14748778209bf9e63f6ada1067e7b=0;
case 'À':int _ddea2f1ca249174a720293b0babfbf4a=0;
case 'Ã':int _5295c44038b2496d484d6275597c9f05=0;
case 'Â':int _569946c2a223d60dd03ecf6e180e2c30=0;
letter = 'A';
if (Boolean.parseBoolean("True")) {break;}break;
case 'É':int _9d1d146759f14e92e3911faeb40dc882=0;
case 'È':int _c9a41124dc943142b18856145c3ecd7d=0;
case 'Ê':int _7036f1c93df90beba314d6bea2359ed6=0;
letter = 'E';
if (Boolean.parseBoolean("True")) {break;}break;
case 'Ì':int _f14939f99a6a16d7ae5e247b024c60c0=0;
case 'Í':int _d3b31146beaea6e86a4597a3283ba1e7=0;
case 'Î':int _1631e51a2816c49af841dcb6fc25ed17=0;
letter = 'I';
if (Boolean.parseBoolean("True")) {break;}break;
case 'Ò':int _7e20479509211b04f4a3e036d3ce4986=0;
case 'Ó':int _8a35eeb5befab9f220c1003ce4424050=0;
case 'Ô':int _d1a6bff8e3dfec89dc6ca918a0fb1f22=0;
case 'Õ':int _4e1368767f755bec06a10b3d34f48ef7=0;
letter = 'O';
if (Boolean.parseBoolean("True")) {break;}break;
case 'Ú':int _454809713b55a6867cdfd3201730e2c3=0;
case 'Ù':int _668ffa5e9980cb707e58f336f3b2e8ca=0;
case 'Û':int _576ff99678af14e80315fa42e4058c80=0;
letter = 'U';
if (Boolean.parseBoolean("True")) {break;}break;
}
return letter;
}
@wniemiec.executionflow.runtime.CollectCalls public boolean tryCatchMethod_try() {
File f = new File("tmp");
FileWriter fw;int _596635afd2469d311fc8b634c8a75721=0;
try {int _187b58036726913ce7d62971c99cbeaa=0;
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
FileWriter fw;int _2991e38ce3103d2aaa3c846fc7f088b9=0;
try {int _b6add0c7f8c6f92d723a60a74d5e1b3a=0;
throw new IOException();
}
catch (IOException e) {
return false;
}
}
@wniemiec.executionflow.runtime.CollectCalls public static void test3(int x, int y, String k) {
x = 0;
while (x < 3) {int _94bdabf6109e16f577df7eb4d09e926b=0;
x++;
}
x = 3;
}
@wniemiec.executionflow.runtime.CollectCalls private static void someMethod() {
}
}
