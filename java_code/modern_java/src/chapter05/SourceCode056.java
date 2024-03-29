package chapter05;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

public class SourceCode056 {
    public static void main(String[] args) {
        Trader raoul = new Trader("Raoul", "Cambridge");
        Trader mario = new Trader("Mario", "Milan");
        Trader alan = new Trader("Alan", "Cambridge");
        Trader brian = new Trader("Brian", "Cambridge");
        List<Transaction> transactions = Arrays.asList(
                new Transaction(brian, 2011, 300),
                new Transaction(raoul, 2012, 1000),
                new Transaction(raoul, 2011, 400),
                new Transaction(mario, 2012, 710),
                new Transaction(mario, 2012, 700),
                new Transaction(alan, 2012, 950)
        );

        //1. 2011년에 일어난 모든 트랜잭션을 찾아 오름차순으로 정리
        List<Transaction> ex1 = transactions.stream()
                .filter(transaction -> transaction.getYear() ==2011)
                .sorted(Comparator.comparing(Transaction::getValue))
                .collect(toList());
        System.out.println(ex1); //[Transaction{trader=Trader{name='Brian', city='Cambridge'}, year=2011, value=300}, Transaction{trader=Trader{name='Raoul', city='Cambridge'}, year=2011, value=400}]


        //2. 거래자가근무하는 모든 도시를 중복없이 나열
        List<String> ex2 = transactions.stream()
                .map(transaction -> transaction.getTrader().getCity())
                .distinct()
                .collect(toList());
        System.out.println(ex2); //[Cambridge, Milan]

        //3.케임브릿지에서 근무하는 모든 거래자를 찾아서 이름순으로 정렬
        List<String> ex3 = transactions.stream()
                .map(Transaction::getTrader)
                .filter(trader -> trader.getCity().equals("Cambridge"))
                .distinct()
                .sorted(Comparator.comparing(Trader::getName))
                .map(Trader::getName)
                .collect(toList());
        System.out.println(ex3); //[Alan, Brian, Raoul]

        //4. 모든 거래자의 이름을 알파벳순으로 정렬해서 반환
        String ex4 = transactions.stream()
                .map(transaction -> transaction.getTrader().getName())
                .distinct()
                .sorted()
                .reduce("", (n1,n2) -> n1+n2); //이름을 알파벳순으로 정렬
        System.out.println(ex4); //AlanBrianMarioRaoul

        //5. Milan에 거래자가 있는가?
        Boolean ex5 = transactions.stream()
                .anyMatch(transaction -> transaction.getTrader().getCity().equals("Milan"));
        System.out.println(ex5); //Milan

        //6. 케임브리지에 거주하는 거래자의 모든 트랜잭션값 출력
         transactions.stream()
                .filter(t -> "Cambridge".equals(t.getTrader().getCity()))
                .map(Transaction::getValue)
                .forEach(System.out::println);

        //7. 전체 트랜잭션중 최댓값은?
        Optional<Integer> ex7 = transactions.stream()
                .map(Transaction::getValue)
                .reduce(Integer::max); //결과 스트림의 최대값 계산
        System.out.println(ex7); //Optional[1000]

        //8. 트랜잭션중 최소값
        Optional<Transaction> ex8 = transactions.stream()
                .reduce((t1, t2) ->  t1.getValue() < t2.getValue() ? t1 :t2); //각 트랜잭션값을 반복비교해서 가장작은 트랜잭션검색
        System.out.println(ex8);


//        JSONObject obj = new JSONObject();
//        obj.put("Modello", "test");
//        String key1 = "Numero modello articolo";
//        String key2 = "Nome modello";
//        String key3 = "Modello";
//        String key4 = "Riferimento produttore";
//        String key5 = "Numero di parti";
//
//
//
//        List<String> list = Arrays.asList("Numero modello articolo", "Nome modello", "Modello",  "Riferimento produttore","Numero di parti");
//
//        Optional<String> value = list.stream().filter(key -> obj.containsKey(key)).findFirst();
//        String mpc = value.orElse("");
//        String mpc = mpcKeys.stream().filter(key -> obj.containsKey(key)).findFirst().orElse("");


    }




    static class Transaction{
        private Trader trader;
        private int year;
        private int value;

        public Transaction(Trader trader, int year, int value) {
            this.trader = trader;
            this.year = year;
            this.value = value;
        }

        public Trader getTrader() {
            return trader;
        }

        public void setTrader(Trader trader) {
            this.trader = trader;
        }

        public int getYear() {
            return year;
        }

        public void setYear(int year) {
            this.year = year;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return "Transaction{" +
                    "trader=" + trader +
                    ", year=" + year +
                    ", value=" + value +
                    '}';
        }
    }

    static class Trader{
        private String name;
        private String city;

         public Trader(String name, String city){
             this.name = name;
             this.city = city;
         }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return "Trader{" +
                    "name='" + name + '\'' +
                    ", city='" + city + '\'' +
                    '}';
        }
    }
}
