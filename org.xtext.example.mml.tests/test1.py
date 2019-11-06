import pandas as pd
from sklearn.model_selection import train_test_split
from sklearn import tree 
from sklearn.metrics import mean_absolute_error
mml_data = pd.read_csv('Boston.csv', sep=',')
X = mml_data.drop(columns=["medv"])
Y = mml_data['mdev']
test_size=0.3
X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=test_size)
clf tree.DecisionTreeRegressor()
clf.fit(X_train, y_train)
DecisionTreeRegressor(max_depth = 2)
metric=mean_absolute_error(y_test, clf.predict(X_test))
print(metric)