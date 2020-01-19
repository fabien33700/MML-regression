import pandas as pd
from sklearn.ensemble import GradientBoostingRegressor
from sklearn.model_selection import train_test_split
from sklearn.metrics import mean_absolute_error
df = pd.read_csv('Boston.csv', sep=',')
df.head()
y = df["medv"]
X = df.drop(columns=["medv"])
test_size = 0.70
X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=test_size)
clf = GradientBoostingRegressor()
clf.fit(X_train, y_train)
max_value = df.max(axis = 0)["medv"]
mape_accuracy = mean_absolute_error(y_test, clf.predict(X_test))/max_value
print("mean_absolute_percentage_error = " + str(mape_accuracy))