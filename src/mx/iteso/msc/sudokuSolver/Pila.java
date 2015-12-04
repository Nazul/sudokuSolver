/*
 * Copyright 2015 USER.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package mx.iteso.msc.sudokuSolver;

/**
 *
 * @author Erick Gonzalez
 */
public class Pila {
     private int  Value;
     private Pila Next;
     
     Pila (int d){
        Value = d;
        Next = null;
     }
     
     int getValue(){
         return Value;
     }
     
     Pila getNext(){
         return Next;
     }
     
          
     void putValue(int d){
         Value = d;
     }
     
     void putNext(Pila p){
         Next = p;
     }
}
