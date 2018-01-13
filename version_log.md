###### v1.0.3
1. 去除了com.android.support:appcompat-v7:25.0.0，解决了在引入包后，由于support版本不一样而导致的问题
2. 订阅的方法增加了限制，增加的限制是：订阅方法必须以"on"开头，以"Event"结尾，区分大小写，如"onLoginEvent()"，主要匹配方式为"on**Event()"，这是为了规范写法，为混淆做好铺垫
3. 增加了混淆说明
