import pandas as pd
from sklearn.tree import DecisionTreeRegressor
from sklearn.model_selection import train_test_split
from sklearn.metrics import mean_absolute_error
df = pd.read_csv('Boston.csv', sep=',')
df.head()
y = df.iloc[:,-1]
X = df[df.columns[:-1]]
test_size = 0.70
X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=test_size)
clf = DecisionTreeRegressor()
clf.fit(X_train, y_train)
mae_accuracy = mean_absolute_error(y_test, clf.predict(X_test))
print("mean_absolute_error = " + str(mae_accuracy))
print(df)
