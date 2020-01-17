import pandas as pd
from sklearn.svm import SVC
from sklearn.model_selection import cross_validate
from sklearn.metrics import mean_squared_error
from sklearn.metrics import mean_absolute_error
df = pd.read_csv('Boston.csv', sep=',')
df.head()
y = df["medv"]
X = df.drop(columns=["medv"])
clf = SVC(C=5.0, kernel='linear')
scoring = ['precision_macro', 'recall_macro']
scores = cross_validate(clf, X, y, scoring=scoring, cv=6)
print(scores)
mae_accuracy = mean_absolute_error(y_test, clf.predict(X_test))
print("mean_absolute_error = " + str(mae_accuracy))
mse_accuracy = mean_squared_error(y_test, clf.predict(X_test))
print("mean_squared_error = " + str(mse_accuracy))
print(df)
